package merkleClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static merkleClient.HashUtil.md5Java;

public class MerkleValidityRequest {

	/**
	 * IP address of the authority
	 * */
	private final String authIPAddr;
	/**
	 * Port number of the authority
	 * */
	private final int  authPort;
	/**
	 * Hash value of the merkle tree root. 
	 * Known before-hand.
	 * */
	private final String mRoot;
	/**
	 * List of transactions this client wants to verify 
	 * the existence of.
	 * */
	private List<String> mRequests;
	
	/**
	 * Sole constructor of this class - marked private.
	 * */
	private MerkleValidityRequest(Builder b){
		this.authIPAddr = b.authIPAddr;
		this.authPort = b.authPort;
		this.mRoot = b.mRoot;
		this.mRequests = b.mRequest;
	}
	
	/**
	 * <p>Method implementing the communication protocol between the client and the authority.</p>
	 * <p>The steps involved are as follows:</p>
	 * 		<p>0. Opens a connection with the authority</p>
	 * 	<p>For each transaction the client does the following:</p>
	 * 		<p>1.: asks for a validityProof for the current transaction</p>
	 * 		<p>2.: listens for a list of hashes which constitute the merkle nodes contents</p>
	 * 	<p>Uses the utility method {@link #isTransactionValid(String, List<String>) isTransactionValid} </p>
	 * 	<p>method to check whether the current transaction is valid or not.</p>
	 * */
	public Map<Boolean, List<String>> checkWhichTransactionValid() throws IOException {

		//Inizializzo la mappa da ritornare
		Map<Boolean, List<String>> validity = new HashMap<>();
		ArrayList<String> nodiTrue = new ArrayList<>();
		ArrayList<String> nodiFalse = new ArrayList<>();

		validity.put(true, nodiTrue);
		validity.put(false, nodiFalse);


		try {
			//0. Opens a connection with the authority
			Socket cSocket = new Socket(authIPAddr, authPort);

			System.out.println("Connecting to Server: " + authIPAddr + " on port" + authPort);

			PrintWriter out = new PrintWriter(cSocket.getOutputStream(), true);

			for (String request : mRequests) {

				// 1.: asks for a validityProof for the current transaction
				out.println(request);

				System.out.println("sending: " + request);

				//2.: listens for a list of hashes which constitute the merkle nodes contents
				BufferedReader reader = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));

				List<String> nodiServer = new ArrayList<>();
				System.out.println("--- Line: " + reader.toString());

				String line = reader.readLine();
				System.out.println("--- Prova: ");

				while (line != null) {
					System.out.println("--- Message received: " + line);
					nodiServer.add(line);
					line = reader.readLine();
				}

				System.out.println("--- Message received: " + nodiServer);

				/*
				 * 	<p>Uses the utility method {@link #isTransactionValid(String, String, List<String>) isTransactionValid} </p>
				 * 	<p>method to check whether the current transaction is valid or not.</p>
				 */
				if (isTransactionValid(request, nodiServer)) {
					nodiTrue.add(request);
				} else {
					nodiFalse.add(request);
				}
			}

			out.println("close");

			cSocket.close();
		}catch (IOException e){
			e.printStackTrace();
		}
		return validity;
	}
	/**
	 * 	Checks whether a transaction 'merkleTx' is part of the merkle tree.
	 * 
	 *  @param merkleTx String: the transaction we want to validate
	 *  @param merkleNodes String: the hash codes of the merkle nodes required to compute 
	 *  the merkle root
	 *  
	 *  @return: boolean value indicating whether this transaction was validated or not.
	 * */
	private boolean isTransactionValid(String merkleTx, List<String> merkleNodes) {

		String toVerifyHash= md5Java(merkleTx);

		for (String node : merkleNodes) {
			toVerifyHash=md5Java(toVerifyHash+node);
		}

		return toVerifyHash.equals(mRoot);

	}

	/**
	 * Builder for the MerkleValidityRequest class. 
	 * */
	public static class Builder {
		private String authIPAddr;
		private int authPort;
		private String mRoot;
		private List<String> mRequest;	
		
		public Builder(String authorityIPAddr, int authorityPort, String merkleRoot) {
			this.authIPAddr = authorityIPAddr;
			this.authPort = authorityPort;
			this.mRoot = merkleRoot;
			mRequest = new ArrayList<>();
		}
				
		public Builder addMerkleValidityCheck(String merkleHash) {
			mRequest.add(merkleHash);
			return this;
		}
		
		public MerkleValidityRequest build() {
			return new MerkleValidityRequest(this);
		}
	}
}