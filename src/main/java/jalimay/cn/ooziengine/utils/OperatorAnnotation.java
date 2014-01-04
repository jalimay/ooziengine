package jalimay.cn.ooziengine.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.CLASS)
@Target(value = { ElementType.TYPE })
public @interface OperatorAnnotation {
	public String name();

	@Target(value = { ElementType.METHOD })
	public static @interface Field {
		public String name();
	}
}
