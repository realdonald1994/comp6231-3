package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class MyLogFormatter extends Formatter{

	@Override
	public String format(LogRecord record) {
		// TODO Auto-generated method stub
		Date  date = new Date(record.getMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return format.format(date)+"  " + record.getLevel()+": \n" + record.getMessage()+"\n";
		
	}
	

}
