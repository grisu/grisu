package org.vpac.grisu.model.dto;

import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StringMapXmlAdaptor extends XmlAdapter<MapWrapperType, Map<String, String>> {

	@Override
	public MapWrapperType marshal(Map<String, String> arg0) throws Exception {

		MapWrapperType type = new MapWrapperType();
		
		for ( String key : arg0.keySet() ) {
			Host obj = new Host();
			obj.hostNameString = key;
			obj.siteNameString = arg0.get(key);
			type.entry.add(obj);
		}
		return type;
	}

	@Override
	public Map<String, String> unmarshal(MapWrapperType arg0) throws Exception {

		Map<String, String> result = new TreeMap<String, String>();
		
		for ( Host obj : arg0.entry ) {
			result.put(obj.hostNameString, obj.siteNameString);
		}
		
		return result;
	}

}
