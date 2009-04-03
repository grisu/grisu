package org.vpac.grisu.client.view.swing.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.BevelBorder;

import org.vpac.grisu.js.model.utils.SubmissionLocationHelpers;

public class QueueRenderer implements ListCellRenderer {
	
	ListCellRenderer parent = null;
	
	public QueueRenderer(ListCellRenderer parent) {
		this.parent = parent;
	}
	
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		
		if ( value != null ) {
			value = SubmissionLocationHelpers.extractQueue((String)value);
		}
		
		return parent.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

	}

}
