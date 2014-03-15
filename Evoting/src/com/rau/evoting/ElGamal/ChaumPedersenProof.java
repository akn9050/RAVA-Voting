package com.rau.evoting.ElGamal;

import java.math.BigInteger;

import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;

import com.rau.evoting.utils.Pair;
import com.rau.evoting.utils.RandomHelper;

public class ChaumPedersenProof {

	// private ElGamalParameters params ;
	// private ElGamalPrivateKeyParameters prKeyParams;
	// private ElGamalPublicKeyParameters pubKeyParams;
	private ElGamalHelper elGamal;
	private BigInteger y1;
	private BigInteger y2;
	private BigInteger a1;
	private BigInteger a2;
	private BigInteger k;
	private BigInteger c;
	private BigInteger s;
	private BigInteger r;

	private BigInteger g;
	private BigInteger p;
	private BigInteger y;
	//BigInteger x;

	private BigInteger res1;
	private BigInteger res2;
	
	public ChaumPedersenProof(ElGamalHelper el) {
		// GlobalParameters.getInstance();
		// params = GlobalParameters.getParams();
		elGamal = el;
		g = elGamal.getPubKeyParams().getParameters().getG();
		p = elGamal.getPubKeyParams().getParameters().getP();
		y = elGamal.getPubKeyParams().getY();
		//x = elGamal.getPrKeyParams().getX();

		//y1 = g.modPow(x, p);
		//y2 = y.modPow(x, p);

	}
	
	
	public  Pair<BigInteger, BigInteger>  generate(BigInteger pubKey, BigInteger r){
		g = GlobalParameters.getParams().getG();
		p = GlobalParameters.getParams().getP();
		y = pubKey;
		this.r = r;
		
		y1 = g.modPow(r, p);
		y2 = y.modPow(r, p);
		
		ProverIteration1();
		VerifierIteration1();
		ProverIteration2();
		VerifierIteration2();
		
		return new Pair<BigInteger, BigInteger>(res1, res2);
	}
	

	public Pair<BigInteger, BigInteger> ProverIteration1() {
		ElGamalPublicKeyParameters pubKey = elGamal.getPubKeyParams();
		k = RandomHelper.randomNonZeroBigInteger(pubKey.getParameters().getP()
				.subtract(new BigInteger("1")));
		a1 = pubKey.getParameters().getG()
				.modPow(k, pubKey.getParameters().getP());
		a2 = pubKey.getParameters().getG().modPow(k, pubKey.getY());
		return new Pair<BigInteger, BigInteger>(a1, a2);

	}

	public BigInteger ProverIteration2() {
		BigInteger temp = c.multiply(r).mod(
				elGamal.getPubKeyParams().getParameters().getP());
		s = k.subtract(temp).mod(
				elGamal.getPubKeyParams().getParameters().getP());
		return s;
	}

	public BigInteger VerifierIteration1() {
		ElGamalPublicKeyParameters pubKey = elGamal.getPubKeyParams();
		
		String temp = y1.toString().concat(y2.toString());
		long t2 = temp.hashCode();
		c = BigInteger.valueOf(t2);
		//RandomHelper.randomNonZeroBigInteger(pubKey.getParameters().getP()
		//		.subtract(new BigInteger("1")));
		return c;
	}

	public boolean VerifierIteration2() {

		BigInteger g = elGamal.getPubKeyParams().getParameters().getG();
		BigInteger p = elGamal.getPubKeyParams().getParameters().getP();
		BigInteger y = elGamal.getPubKeyParams().getY();
		res1 = g.modPow(s, p).multiply(y1.modPow(c, p).mod(p));
		res2 = y.modPow(s, p).multiply(y2.modPow(c, p).mod(p));

		if(a1.equals(res1) && a2.equals(res2))
			return true;
		
		return false;
	}

	public BigInteger getA1() {
		return a1;
	}

	public BigInteger getA2() {
		return a2;
	}

}
