package net.asrex.skillful.util;

import net.minecraftforge.fml.common.FMLLog;
import java.util.logging.Logger;

public class Log {
	
	public static Logger get(Class c) {
		// TODO: move to @Log4j2 lombok annotation
		
		Logger logger =  Logger.getLogger("skillful-" + c.getCanonicalName());
		//logger.setParent(FMLLog.getLogger());
		
		return logger;
	}
	
}
