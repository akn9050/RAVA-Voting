package com.rau.evoting.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import org.bouncycastle.crypto.engines.ElGamalEngine;
import org.bouncycastle.crypto.generators.ElGamalParametersGenerator;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;

public class ElGamalHelper {

	private static ElGamalParameters params = null;
	private ElGamalPrivateKeyParameters prKeyParams;
	private ElGamalPublicKeyParameters pubKeyParams;
	private ElGamalEngine engine;

/*	public static void genPandG() {
		ElGamalParametersGenerator gen = new ElGamalParametersGenerator();
		gen.init(500, 5, new SecureRandom());
		params = gen.generateParameters();
	}
*/
	public ElGamalHelper() {
		if (params == null) {
			ElGamalParametersGenerator gen = new ElGamalParametersGenerator();
			gen.init(500, 5, new SecureRandom());
			params = gen.generateParameters();
		}
		prKeyParams = new ElGamalPrivateKeyParameters(
				RandomHelper.randomBigInteger(params.getP().subtract(
						new BigInteger("1"))), params);
		pubKeyParams = new ElGamalPublicKeyParameters(params.getG().modPow(
				prKeyParams.getX(), params.getP()), params);
		engine = new ElGamalEngine();
	}
	
	public ElGamalHelper(String pubKey){
		if (params == null) {
			ElGamalParametersGenerator gen = new ElGamalParametersGenerator();
			gen.init(500, 5, new SecureRandom());
			params = gen.generateParameters();
		}
		pubKeyParams = new ElGamalPublicKeyParameters(new BigInteger(pubKey), params);
		engine = new ElGamalEngine();
	}

	public String encode(String text) {
		engine.init(true, pubKeyParams);
		byte[] in = text.getBytes();
		byte[] b = engine.processBlock(in, 0, in.length);
		String encoded = new String(b);
		return encoded;
	}

	public String decode(String codedText) {
		byte[] in = codedText.getBytes();
		byte[] b = engine.processBlock(in, 0, in.length);
		engine.init(false, prKeyParams);
		b = engine.processBlock(b, 0, b.length);
		String decoded = new String(b);
		return decoded;
	}

	public String getElectionPublicKey(List<String> publicKeys) {
		String key = "";
		BigInteger res = new BigInteger("1");
		for (String k : publicKeys) {
			res = res.multiply(new BigInteger(k));
			res = res.mod(pubKeyParams.getParameters().getP());
		}
		key = res.toString();
		return key;
	}
	
	

	public int getPrivateKeyHash() {
		return prKeyParams.getX().hashCode();
	}    

	public String getPrivateKey() {
		return prKeyParams.getX().toString();
	}

	public int getPublicKeyHash() {
		return pubKeyParams.getY().hashCode();
	}

	public String getPublicKey() {
		return pubKeyParams.getY().toString();
	}

	public String getP() {
		return params.getP().toString();
	}

	public String getG() {
		return params.getG().toString();
	}

}
