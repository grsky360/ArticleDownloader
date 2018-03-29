package hsnss.article;

import cn.hsnss.article.config.Hosts;
import cn.hsnss.article.config.Tasks;
import cn.hsnss.article.down.Site;
import cn.hsnss.article.down.Tieba;
import cn.hsnss.article.down.Yamibo;
import cn.hsnss.util.EpubPacker;

import java.io.UnsupportedEncodingException;

public class Main {

	public static void main(String[] args) throws UnsupportedEncodingException {
		Hosts.Host[] hosts = new Hosts().getHosts();
		Tasks tasks = new Tasks();

		String savePath = tasks.getPath();
		Tasks.Task[] task = tasks.getTasks();

		for (Tasks.Task t : task) {
			String name = t.getName();
			String url = t.getValue();
			String pn = null;
			String hostId = null;
			for (Hosts.Host h : hosts) {
				if (url.contains(h.getHost())) {
					pn = h.getPn();
					hostId = h.getId();
					break;
				}
			}
			if (pn != null) {
				Site site = null;
				if (hostId.equals("tieba")) {
					site = new Tieba(savePath, name, url, pn);
				} else if (hostId.equals("yamibo")) {
					site = new Yamibo(savePath, name, url, pn);
				}
				if (site != null) {
					site.start();
					if (site.getImgs() != null) {
						EpubPacker packer = new EpubPacker(savePath, name, site.getTitle(), site.getContent(), site.getImgs());
						packer.pack();
					} else {
						site.save();
					}
				} else {
					System.out.println("Error! No class.");
				}


			} else {
				System.out.println("Error! No support for this host.");
			}
		}
	}

}
