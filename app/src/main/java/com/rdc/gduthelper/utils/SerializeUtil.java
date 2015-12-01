package com.rdc.gduthelper.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by seasonyuu on 15/12/2.
 */
public class SerializeUtil {
	public static String serialize(Serializable serializable) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				byteArrayOutputStream);
		objectOutputStream.writeObject(serializable);
		String serStr = byteArrayOutputStream.toString("ISO-8859-1");
		serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
		objectOutputStream.close();
		byteArrayOutputStream.close();
		return serStr;
	}

	public static Serializable deSerialization(String str) throws IOException,
			ClassNotFoundException {
		String redStr = java.net.URLDecoder.decode(str, "UTF-8");
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				redStr.getBytes("ISO-8859-1"));
		ObjectInputStream objectInputStream = new ObjectInputStream(
				byteArrayInputStream);
		Serializable serializable = (Serializable) objectInputStream.readObject();
		objectInputStream.close();
		byteArrayInputStream.close();
		return serializable;

	}

}
