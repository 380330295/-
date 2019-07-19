import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Kiku
 * @date 2019/7/19 17:24
 */

//得到1分钟之前的时间测试
public class TestCalendar  {

    @Test
    public void test001(){

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE, -1);
        Date time = calendar.getTime();
        System.out.println(time);
    }
    @Test
    public void test002(){

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.set(GregorianCalendar.MINUTE, -1);
        Date time = calendar.getTime();
        System.out.println(time);
    }
}
