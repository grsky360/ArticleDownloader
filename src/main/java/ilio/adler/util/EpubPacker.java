package ilio.adler.util;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.io.File;
import java.util.Set;

public class EpubPacker {
	private static final Element HEAD = new Element("head")
			.appendChild(new Element("meta")
					.attr("http-equiv", "Content-Type")
					.attr("content", "text/html; charset=utf-8"))
			.appendChild(new Element("meta")
					.attr("name", "provider")
					.attr("content", "ilio.tech"))
			.appendChild(new Element("meta")
					.attr("name", "builder")
					.attr("content", "Hyia"))
			.appendChild(new Element("meta")
					.attr("name", "right")
					.attr("content", "Provided by Hyia")
			);


	private static Element head(String title) {
		return HEAD.clone().appendChild(new Element("title").text(title));
	}
	private String path;
	private String filename;
	private String title;
	private Set<String> images;

	private String[] lines;

	public EpubPacker(String path, String filename, String title, String[] lines, List<String> images) {
		this.path = path;
		this.filename = filename;
		this.title = title;
		this.lines = lines;

		this.images = new HashSet<>(images);
		System.out.println("Image number: " + this.images.size());
	}

	private Document split() {
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append("<p>").append(line).append("</p>");
		}
		String content = sb.toString();
		for (String img : images) {
			content = content.replaceAll(img, new Element("div")
					.attr("style", "text-align:center")
					.appendChild(new Element("img").attr("src", img.hashCode() + ".jpg").attr("alt", img)).outerHtml());
		}
		Document html = Jsoup.parse(new Element("html")
				.appendChild(head(title))
				.appendChild(Jsoup.parse(content).body())
				.html());
		html.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		html.selectFirst("html").attr("xmlns", "http://www.w3.org/1999/xhtml");
		return html;
	}

	public boolean pack() {
		Document html = split();
		if (html == null) return false;

		Book book = new Book();
		book.getMetadata().addTitle(title);
		for (String img : images) {
			try {
				InputStream imgBytes = HttpClient.download(img);
				if (imgBytes != null) {
					Resource resource = new Resource(imgBytes, img.hashCode() + ".jpg");
					book.addResource(resource);
					if (book.getCoverImage() == null) {
						book.setCoverImage(resource);
					}
				}
			} catch (IOException ignored) {
			}
		}
		try {
			book.addSection(title, new Resource(new ByteArrayInputStream(html.html().getBytes(StandardCharsets.UTF_8)), "content.html"));

			EpubWriter writer = new EpubWriter();
			writer.write(book, new FileOutputStream(path + File.separator + filename + ".epub"));
		} catch (IOException e) {
			return false;
		}
		return true;
	}

}
