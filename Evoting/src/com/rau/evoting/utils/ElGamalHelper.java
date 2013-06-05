package com.rau.evoting.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.engines.ElGamalEngine;
import org.bouncycastle.crypto.generators.ElGamalParametersGenerator;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;

public class ElGamalHelper {

	private ElGamalParameters params;
	private ElGamalPrivateKeyParameters prKeyParams;
	private ElGamalPublicKeyParameters pubKeyParams;
	private ElGamalEngine engine;

	/*
	 * public class PublicKey{ BigInteger g; BigInteger p; BigInteger y; }
	 * 
	 * public class PrivateKey{ BigInteger x; }
	 */
	
	public ElGamalHelper(){
		ElGamalParametersGenerator gen = new ElGamalParametersGenerator();
		gen.init(182, 5, new SecureRandom());
		params = gen.generateParameters();
		prKeyParams = new ElGamalPrivateKeyParameters(new BigInteger("2"),
				params);
		pubKeyParams = new ElGamalPublicKeyParameters(params.getG().modPow(
				prKeyParams.getX(), params.getP()), params);
		engine = new ElGamalEngine();
	}

	/*public void generateKey() {
		ElGamalParametersGenerator gen = new ElGamalParametersGenerator();
		gen.init(182, 5, new SecureRandom());
		params = gen.generateParameters();
		prKeyParams = new ElGamalPrivateKeyParameters(new BigInteger("2"),
				params);
		pubKeyParams = new ElGamalPublicKeyParameters(params.getG().modPow(
				prKeyParams.getX(), params.getP()), params);
	} */

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
	
	public int getPrivateKeyHash(){
		return prKeyParams.getX().hashCode();
	}
}
