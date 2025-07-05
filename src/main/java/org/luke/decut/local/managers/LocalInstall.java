package org.luke.decut.local.managers;

import java.io.File;

public class LocalInstall {
	private File root;
	private String version;
	private File binary;

	public LocalInstall(File root, File binary, String version) {
		this.root = root;
		this.version = version;
		this.binary = binary;
	}
	public File getRoot() {
		return root;
	}
	public void setRoot(File root) {
		this.root = root;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public File getBinary() {
		return binary;
	}
	public void setBinary(File binary) {
		this.binary = binary;
	}
}
