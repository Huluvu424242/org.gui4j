package org.gui4j.core.swing.calendar;

/*

  File: $Workfile: CalendarButton.java $

  Original Author: Michael Karneim

  Last modified on: $Modtime:  $

  Last modified by: $Author: joachims $



  Maturity Level: DRAFT

*/

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
  * CalendarButton provides a simple mechanism for the user to bind a
  * date chooser popup to a text component. Whenever the button is pressed
  * a popup appears below a specified text component, and displays a
  * CalenderBean control.
  *    JTextField dateField = new JTextField("14.01.1971   ");
  *    CalendarButton dateButton = new CalendarButton();
  *    dateButton.setText( "...");
  *    dateButton.setTextComponent( dateField);
  *    dateButton.setPreferredSize( new Dimension( 20, dateField.getPreferredSize().height));
  *    JFrame frame = new JFrame();
  *    frame.getContentPane().setLayout( new FlowLayout(0));
  *    frame.getContentPane().add( dateField);
  *    frame.getContentPane().add( dateButton);
  *    frame.pack();
  *    frame.setVisible( true);
  */
public class CalendarButton extends JButton implements ActionListener
{

    javax.swing.text.JTextComponent textComponent;

    JPopupMenu popup = null;

    //  SimpleDateFormat format = new SimpleDateFormat(FormatManager.getInstance().getDatePattern()); //[PENDING,mk,29.02.2000: Hier keine Abhängigkeit ins Restprojekt einbauen!]

    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy");

    CalendarBean calendarBean;

    /**
      * This method invokes a simple test program that demonstrates
      * the calendar button.
      * @param args
      */
    public static void main(String[] args)
    {
        System.out.println("test program for CalendarButton");
        JTextField dateField = new JTextField("14.01.1971   ");

        CalendarButton dateButton = new CalendarButton();

        dateButton.setText("...");

        dateButton.setTextComponent(dateField);

        dateButton.setPreferredSize(new java.awt.Dimension(20, dateField.getPreferredSize().height));

        JFrame frame1 = new JFrame();

        frame1.getContentPane().setLayout(new java.awt.FlowLayout(0));

        frame1.getContentPane().add(dateField);

        frame1.getContentPane().add(dateButton);
        JComboBox comboBox = new JComboBox(new String[] {"1", "2" });
		frame1.getContentPane().add(comboBox);
        frame1.pack();

        frame1.setVisible(true);

    }

    /**
    
    * Creates an instance of CalenderBean
    
    */

    public CalendarButton()
    {
        this.addActionListener(this);
        this.setEnabled(this.textComponent != null ? this.textComponent.isEditable() : false);
    }

    /**
      * Implements the ActionListener interface. This method is called
      * whenever the button is pressed.
      * @param e
      */

    public void actionPerformed(ActionEvent e)
    {
        if (textComponent == null)
            return;

        if (e.getSource() == this)
        {
            JPopupMenu p = this.getPopupMenu();
            String dateString = this.textComponent.getText();

            try
            {
                Date date = this.format.parse(dateString);
                this.getCalendarBean().select(date);
            }
            catch (Throwable t)
            {
                this.getCalendarBean().showMonth(Calendar.getInstance());
                this.getCalendarBean().select(new Date());

            }

            p.show(textComponent, 0, textComponent.getSize().height);

        }
        else
        {
            if (e.getSource() == this.getCalendarBean())
            {
				this.textComponent.grabFocus();
                JPopupMenu p = this.getPopupMenu();
                p.setVisible(false);
                Date date = this.getCalendarBean().getSelectedDate();
                String dateString = format.format(date);
                this.textComponent.setText(dateString);
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        grabFocus();
                    }
                });
            }
        }

    }

    private transient PropertyChangeListener enableStateListener = new PropertyChangeListener()
    {
        public void propertyChange(PropertyChangeEvent evt)
        {
            if (!(evt.getSource() == textComponent))
            {
				return;
            }
            boolean newValue = ((Boolean) evt.getNewValue()).booleanValue();
            setEnabled(newValue);
        }
    };
    /**
    
      * Sets the reference to the text component, the calender button should work
    
      * with. From this text component the calendar button will get the date
    
      * string to put into the date chooser popup on button click. Below this
    
      * text component the date chooser popup will be displayed. Into this text component
    
      * the selected date will be inserted from the date chooser component.
    
      * @param newTextComponent the text component the button will work with
    
    */
    public void setTextComponent(javax.swing.text.JTextComponent newTextComponent)
    {

        javax.swing.text.JTextComponent oldTextComponent = textComponent;

        if (this.textComponent != null)
            this.textComponent.removePropertyChangeListener("editable", enableStateListener);

        textComponent = newTextComponent;

        if (this.textComponent != null)
        {
            this.textComponent.addPropertyChangeListener("editable", enableStateListener);
            this.setEnabled(this.textComponent.isEditable());
        }

        firePropertyChange("textComponent", oldTextComponent, newTextComponent);

    }

    /**
    
    * Returns the text component that is bound to this button for displaying the
    
    * date.
    
    * @return the text component that is bound to this button for displaying the
    
    * date
    
    */

    public javax.swing.text.JTextComponent getTextComponent()
    {

        return textComponent;

    }

    /**
    
    * Returns the popup menu with the date chooser control.
    
    * @return the popup menu with the date chooser control
    
    */

    protected JPopupMenu getPopupMenu()
    {

        if (this.popup == null)
        {
            this.popup = new JPopupMenu();
            this.popup.add(this.getCalendarBean());
        }

        return this.popup;

    }

    /**
    
    * Defines the pattern that has to be used to transform a String to
    
    * a Date instance and vice versa. The default is "dd.mm.yyyy".
    
    * @param pattern the format pattern
    
    */

    public void setDatePattern(String pattern)
    {

        String oldPattern = this.format.toPattern();

        this.format.applyPattern(pattern);

        firePropertyChange("datePattern", oldPattern, pattern);

    }

    /**
    
    * Returns the pattern that has to be used to transform a String to
    
    * a Date instance and vice versa.
    
    * @return the pattern that has to be used to transform a String to
    
    * a Date instance and vice versa
    
    */

    public String getDatePattern()
    {

        return this.format.toPattern();

    }

    /**
    
    * Sets the CalendarBean instance that has to be used in the popup.
    
    * <P>Note:<BR>
    
    * This method offers an easy way to customize the look and feel
    
    * of the date chooser popup. Be carefull not to use one single calendar
    
    * bean in more calendar buttons.
    
    * @param newCalendarBean the CalendarBean instance that has to be used in the popup
    
    */

    public void setCalendarBean(CalendarBean newCalendarBean)
    {

        CalendarBean oldCalendarBean = calendarBean;

        if (oldCalendarBean != null)
        {

            oldCalendarBean.removeActionListener(this);

        }

        this.calendarBean = newCalendarBean;

        this.calendarBean.addActionListener(this);

        firePropertyChange("calendarBean", oldCalendarBean, newCalendarBean);

    }

    /**
    
    * Returns the CalendarBean instance that is used in the popup.
    
    * @return the CalendarBean instance that is used in the popup
    
    */

    public CalendarBean getCalendarBean()
    {

        if (this.calendarBean == null)
        {

            this.calendarBean = new CalendarBean();
            Font f = textComponent.getFont(); // TODO: dynamisch 
            this.calendarBean.setDayFont(f);
            this.calendarBean.setDateFont(f);
            this.calendarBean.setHeaderFont(f);
            this.calendarBean.setFont(f);
            this.calendarBean.addActionListener(this);
        }

        return this.calendarBean;

    }

}