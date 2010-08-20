package org.vpac.grisu.frontend.view.swing.utils;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

public class EmptySelectionListCellRenderer extends DefaultListCellRenderer {

        private String selectionPrompt;
        private JComboBox comboBox;

        public EmptySelectionListCellRenderer( JComboBox comboBox,
                                     String selectionPrompt ) {
            this.comboBox = comboBox;
            this.selectionPrompt = selectionPrompt;
        }

        public Component getListCellRendererComponent( JList list,
                Object value, int ix, boolean sel, boolean focus ) {
            super.getListCellRendererComponent( list, value, ix,
                    sel, focus );
            if ( ix == -1 && comboBox.getSelectedIndex() == -1 )
                setText( selectionPrompt );
            return this;
        }
    }
