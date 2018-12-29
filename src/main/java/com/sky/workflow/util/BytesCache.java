/*
 * BytesCache.java
 * modico.net (lihw@jbbis.com.cn), 2008-07-19
 */

package com.sky.workflow.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class BytesCache extends ByteArrayOutputStream {
	//////////////////////////////////////////////////////////////////
	// Enhance writeTo(OutputStream)

	public void writeTo(OutputStream out, int off) throws IOException {
		writeTo(out, off, count - off);
	}

	public synchronized void writeTo(OutputStream out, int off, int len) throws IOException {
		out.write(buf, off, len);
	}

	//////////////////////////////////////////////////////////////////
	// Enhance toString() and toString(String)

	public String toString(int off) {
		return toString(off, count - off);
	}

	public synchronized String toString(int off, int len) {
		return new String(buf, off, len);
	}

	public String toString(int off, String charsetName) throws UnsupportedEncodingException {
		return toString(off, count - off, charsetName);
	}

	public synchronized String toString(int off, int len, String charsetName) throws UnsupportedEncodingException {
		return new String(buf, off, len, charsetName);
	}

	//////////////////////////////////////////////////////////////////
	// Add a new method.

	public synchronized ByteArrayInputStream getInputStream() {
		return new ByteArrayInputStream(buf, 0, count);
	}

	public synchronized ByteArrayInputStream getInputStream(int pos, int len) {
		return new ByteArrayInputStream(buf, pos, len);
	}

	//////////////////////////////////////////////////////////////////
	// Enhance reset

	public void reset(int pos) {
		if (pos < 0 || pos > count) {
			throw new IllegalArgumentException("pos:" + pos);
		}

		count = pos;
	}

	public byte[] toByteArray(int pos, int len) {
		byte[] t = new byte[len];
		System.arraycopy(buf, pos, t, 0, len);
		return t;
	}
}
