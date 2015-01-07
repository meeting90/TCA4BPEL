package cn.edu.nju.cs.ctao4bpel.store;

import java.io.File;

import javax.xml.namespace.QName;

public final class CBAInfo {
	final QName aspectName;
	final File cba;
	public CBAInfo(QName aspectName, File cba) {
		this.aspectName =aspectName;
		this.cba = cba;
	}
}
