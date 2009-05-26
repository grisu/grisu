import java.io.File;
import java.util.List;

import org.vpac.grisu.control.ServiceInterface;
import org.vpac.grisu.control.SeveralXMLHelpers;
import org.vpac.grisu.js.model.utils.SubmissionLocationHelpers;
import org.vpac.grisu.model.GridResource;
import org.w3c.dom.Document;


public class GridResourceQuery {
		
	public static void main(String[] args) throws Exception {
		
		ServiceInterface si = Login.getServiceInterfaceForUrl("http://localhost:8080/grisu-ws/services/grisu", args[0], args[1].toCharArray());
		
		Document jsdl = SeveralXMLHelpers.loadXMLFile(new File("/home/markus/workspace/grisu/frontend/grisu-examples/src/main/resources/gridResourceQueryJsdl.xml"));		
		
		List<GridResource> result = si.findMatchingSubmissionLocations(jsdl, "/ARCS/StartUp");
		
		for ( GridResource temp : result ) {
			System.out.println("SubmissionLocation: "+SubmissionLocationHelpers.createSubmissionLocationString(temp));
		}
		
		
		
	}

}
