

import java.io.*;
import java.net.*;

public class UDPSIPServer {
	static DatagramSocket serverSocket;

	public static void main(String[] args) throws Exception {

		int porta = 5080;
		int numConn = 1;

		serverSocket = new DatagramSocket(porta);

		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];

		while (true) {

			DatagramPacket pacoteRecebido = new DatagramPacket(receiveData, receiveData.length);
			System.out.println("Esperando na porta: " + porta);

			serverSocket.receive(pacoteRecebido);
			System.out.print("Datagrama UDP [" + numConn + "] recebido...");

			String protocoloRecebido = new String(pacoteRecebido.getData());

			System.out.println("Protocolo recebido :\n" + protocoloRecebido + "\n");

			if (protocoloRecebido != null && protocoloRecebido.length() > 0) {

				// Pegando o IP e porta do pacoteRecebido
				InetAddress IPPacoteRecebido = pacoteRecebido.getAddress();

				int portaPacoteRecebido = pacoteRecebido.getPort();

				// Pegando o metodo do protocoloRecebido

				String metodo = protocoloRecebido.split("\r\n")[0].split(" ")[0]; 
				
				String verifack = protocoloRecebido.split("\r\n")[0].split(" ")[0];
				
				String veriOK = protocoloRecebido.split("\r\n")[0];
				
				System.out.println("\nVerifica OK: "+veriOK + "\n\n\n");
				
				System.out.println("\nVerifica ACK: "+verifack + "\n\n\n");

				System.out.println("\nMetodo: " + metodo + "\n\n\n");

				if (metodo.equals("INVITE")) {

					String via = protocoloRecebido.split("Via: ")[1].split("\r\n")[0].replace("\n", "").replace("\r",
							"");
					String from = protocoloRecebido.split("From: ")[1].split("\r\n")[0].replace("\n", "").replace("\r",
							"");
					String to = protocoloRecebido.split("To: ")[1].split("\r\n")[0].replaceAll("\n", "").replace("\r",
							"");
					String call = protocoloRecebido.split("Call-ID: ")[1].split("\r\n")[0].replaceAll("\n", "")
							.replace("\r", "");
					String cseq = protocoloRecebido.split("CSeq: ")[1].split("\r\n")[0].replaceAll("\n", "")
							.replace("\r", "");
					String contact = protocoloRecebido.split("Contact: ")[1].split("\r\n")[0].replaceAll("\n", "")
							.replace("\r", "");
					String maxfoward = protocoloRecebido.split("Max-Forwards: ")[1].split("\r\n")[0]
							.replaceAll("\n", "").replaceAll("\r", "");
					String contentt = protocoloRecebido.split("Content-Type: ")[1].split("\r\n")[0].replace("\n", "")
							.replace("\r", "");
					String contentl = protocoloRecebido.split("Content-Length: ")[1].split("\r\n")[0].replace("\n", "")
							.replace("\r", "");
					String allow = protocoloRecebido.split("Allow: ")[1].split("\r\n")[0].replace("\n", "")
							.replace("\r", "");

					// Criando 100 Trying
					String trying = "SIP/2.0 100 Trying\r\n";
					trying += "Via: " + via + "\r\n";
					trying += "To: " + to + "\r\n";
					trying += "From: " + from + ".\r\n";
					trying += "Call-ID: " + call + "\r\n";
					trying += "CSeq: " + cseq + "\r\n\r\n";
					//trying += "Content-Length: 0\r\n\r\n";
					
					System.out.println("ESSE EH O TRYNING :\n"+trying);

					// Enviando Trying
					enviar(IPPacoteRecebido, portaPacoteRecebido, trying.getBytes());
					Thread.currentThread().sleep(100);

					// Criando 180 Ringing
					String ringing = "SIP/2.0 180 Ringing\r\n";
					ringing += "Via: " + via + "\r\n";
					ringing += "To: <" + to + ":5080>\r\n";
					ringing += "From: " + from + "\r\n";
					ringing += "Call-ID: " + call + "\r\n";
					//ringing += "Contact: "+contact+"\r\n";
					ringing += "CSeq: " + cseq + "\r\n\r\n";
					//ringing += "Content-Length: 0\r\n\r\n";
					
					System.out.println("ESSE EH O RINGING :\n"+ringing);

					// Enviando Ringing
					enviar(IPPacoteRecebido, portaPacoteRecebido, ringing.getBytes());
					Thread.currentThread().sleep(100);

					// Criando SDP, é preciso criar para enviar junto com o 200 OK
					String sdp = "Content-Length: 233\r\n\r\n";
					sdp += "v=0" + "\r\n";
					sdp += "o=Z 0 0 IN IP4 127.0.0.1" + "\r\n";
					sdp += "s=Z" + "\r\n";
					sdp += "c=IN IP4 127.0.0.1" + "\r\n";
					sdp += "t=0 0\r\n";
					sdp += "m=audio 8000 RTP/AVP 3 110 8 0 98 101" + "\r\n";
					sdp += "a=rtpmap:110 speex/8000" + "\r\n";
					sdp += "a=rtpmap:98 iLBC/8000" + "\r\n";
					sdp += "a=fmtp:98 mode=20\r\n";
					sdp += "a=rtpmap:101 telephone-event/8000" + "\r\n";
					sdp += "a=fmtp:101 0-15" + "\r\n";
					sdp += "a=sendrecv" + "\r\n";

					// Criando 200 OK
					String ok = "SIP/2.0 200 OK\r\n";
					ok += "Via: " + via + "\r\n";
					ok += "To: <" + to + ":5080>\r\n";
					ok += "From: " + from + "\r\n";
					ok += "Call-ID: " + call + "\r\n";
					ok += "CSeq: " + cseq + "\r\n";
					ok += "Allow: "+ allow + "\r\n";
					ok += "Contact: <sip:miguelbrito@127.0.0.1:5080>\r\n";
					ok += "Content-Type: " + contentt + "\r\n";
					ok += sdp;

					System.out.println("ESSE EH O OK: \n" + ok + "\n\n\n\n");

					// Enviando 200 OK
					enviar(IPPacoteRecebido, portaPacoteRecebido, ok.getBytes());
					Thread.currentThread().sleep(100);

				}
				else if (verifack.equals("ACK")) {
					System.out.println("Parabens conexao estabelecida");

					Thread.currentThread().sleep(3000);
					String via = protocoloRecebido.split("Via: ")[1].split("\r\n")[0].replace("\n", "").replace("\r",
							"");
					String branchTemp = via.split("SIP/2.0/UDP ")[1].split(";")[2].split("=")[1];

					String branch = branchTemp.replace(".", "/").split("/")[0];

					String from = protocoloRecebido.split("From: ")[1].split("\r\n")[0].replace("\n", "").replace("\r",
							"");
					String to = protocoloRecebido.split("To: ")[1].split("\r\n")[0].replaceAll("\n", "").replace("\r",
							"");
					String call = protocoloRecebido.split("Call-ID: ")[1].split("\r\n")[0].replaceAll("\n", "")
							.replace("\r", "");

					// Criando BYE

					String bye = "BYE sip:127.0.0.1:5080 SIP/2.0\r\n";
					bye += "Via: SIP/2.0/UDP 127.0.0.1:5060;branch=" + branch + ".OugVfUCEQ;rport\r\n";
					bye += "From: "+to+"\r\n";
					bye += "To: "+ from+"\r\n";
					bye += "CSeq: 21 BYE\r\n";
					bye += "Call-ID: "+call+"\r\n";
					bye += "Max-Forwards: 70\r\n\r\n";

					// enviando BYE
					System.out.println("PRINTANDO BYE PARA ENVIAR :\n" + bye);
					enviar(IPPacoteRecebido, portaPacoteRecebido, bye.getBytes());

				}
				else if (veriOK.contains("OK")) {
					System.out.println("Conexao terminada!!");
					//serverSocket.close();
				}
				else if (metodo.equals("BYE")) {

					String via = protocoloRecebido.split("Via: ")[1].split("\r\n")[0].replace("\n", "").replace("\r",
							"");
					String branchTemp = via.split("SIP/2.0/UDP ")[1].split(";")[1].split("=")[1];
					
					String from = protocoloRecebido.split("From: ")[1].split("\r\n")[0].replace("\n", "").replace("\r",
							"");
					String to = protocoloRecebido.split("To: ")[1].split("\r\n")[0].replaceAll("\n", "").replace("\r",
							"");
					String call = protocoloRecebido.split("Call-ID: ")[1].split("\r\n")[0].replaceAll("\n", "")
							.replace("\r", "");
					

					// Criando OK - BYE

					String okbye = "SIP/2.0 200 OK\r\n";
					okbye += "Via: " + via + "\r\n";
					okbye += "From: " + from + "\r\n";
					okbye += "To: " + to + "\r\n";
					okbye += "Call-ID: " + call + "\r\n";
					okbye += "Cseq: 21 BYE\r\n";
					okbye += "Content-Length: 0\r\n\r\n";

					// enviando OK - BYE
					System.out.println("PRINTANDO OK - BYE PARA ENVIAR, CASO ELE MANDE UM BYE :\n" + okbye);
					enviar(IPPacoteRecebido, 5080, okbye.getBytes());
				}

			}

		}
	}

	public static void enviar(InetAddress ipEnviar, int portaEnviar, byte[] protocoloEnviar) throws Exception {
		DatagramPacket pacoteRecebido = new DatagramPacket(protocoloEnviar, protocoloEnviar.length, ipEnviar,
				portaEnviar);
		try {
			serverSocket.send(pacoteRecebido);
		} catch (IOException e) {
			e.printStackTrace();

		}
	}
}