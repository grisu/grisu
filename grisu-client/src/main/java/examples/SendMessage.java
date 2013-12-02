package examples;

import grisu.control.ServiceInterface;
import grisu.frontend.control.login.LoginManager;
import grisu.model.dto.DtoMessage;
import grisu.model.info.dto.DtoStringList;

public class SendMessage {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		ServiceInterface si = LoginManager.login("local");

        DtoMessage msg = new DtoMessage("title");
        msg.setMessage("Hello message");
        DtoStringList u = DtoStringList.fromSingleString("Markus");
        String result = si.sendMessage(u, msg);

        System.out.println(result);


//        DtoMessages messages = si.getMessages();
//
//        for (DtoMessage m : messages.getMessages() ) {
//            System.out.println(m.getTitle());
//        }

        System.exit(0);
	}

}
