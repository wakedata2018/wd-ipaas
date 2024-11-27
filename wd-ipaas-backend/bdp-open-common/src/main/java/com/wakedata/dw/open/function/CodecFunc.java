/*
 *  Copyright (C) 2021 the original author or authors.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wakedata.dw.open.function;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;

/**
 * Codec Functions
 * 
 * @author Francis Dong
 *
 */
public class CodecFunc implements IFunc {

	private static final Logger LOGGER = LoggerFactory.getLogger(CodecFunc.class);

	private static final String IV = "12345678";

	private static CodecFunc singleton;

	public static CodecFunc getInstance() {
		if (singleton == null) {
			synchronized (CodecFunc.class) {
				if (singleton == null) {
					CodecFunc instance = new CodecFunc();
					instance.init();
					singleton = instance;
				}
			}
		}
		return singleton;
	}

	private CodecFunc() {
	}

	@Override
	public void init() {
		FunctionEnumSet.CodecEnum[] codecEnums = FunctionEnumSet.CodecEnum.values();
		Arrays.stream(codecEnums).forEach(x-> FuncExecutor.register(x.method(), this));
	}

	public String md5(String data) {
		return DigestUtils.md5Hex(data);
	}

	public String sha1(String data) {
		return DigestUtils.sha1Hex(data);
	}

	public String sha256(String data) {
		return DigestUtils.sha256Hex(data);
	}

	public String sha384(String data) {
		return DigestUtils.sha384Hex(data);
	}

	public String sha512(String data) {
		return DigestUtils.sha512Hex(data);
	}

	public String base64Encode(String data) throws Exception {
			return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
	}

	public String base64Decode(String data) throws Exception {
		return new String(Base64.getDecoder().decode(data));
	}

	public String aesEncrypt(String data, String key) throws Exception {
		if (StringUtils.isBlank(data) || StringUtils.isBlank(key)) {
			return null;
		}
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			byte[] result = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(result);
		} catch (Exception e) {
			LOGGER.error("AES encrypt error, data={}", data, e);
			throw e;
		}
	}

	public String aesDecrypt(String data, String key) throws Exception {
		if (StringUtils.isBlank(data) || StringUtils.isBlank(key)) {
			return null;
		}
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes( StandardCharsets.UTF_8), "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] result = Base64.getDecoder().decode(data);
			return new String(cipher.doFinal(result));
		} catch (Exception e) {
			LOGGER.error("AES decrypt error, data={}", data, e);
			throw e;
		}
	}

	public String desEncrypt(String data, String key) throws Exception {
		if (StringUtils.isBlank(data) || StringUtils.isBlank(key)) {
			return null;
		}
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			byte[] bytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
			return new String(Base64.getEncoder().encode(bytes));
		} catch (Exception e) {
			LOGGER.error("DES eecrypt error, data={}", data, e);
			throw e;
		}
	}

	public String desDecrypt(String data, String key) throws Exception {
		if (StringUtils.isBlank(data) || StringUtils.isBlank(key)) {
			return null;
		}
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes(StandardCharsets.UTF_8));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
			cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
			return new String(cipher.doFinal(Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
		} catch (Exception e) {
			LOGGER.error("DES decrypt error, data={}", data, e);
			throw e;
		}
	}

}
