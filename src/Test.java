
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Parameterized.*;

import java.io.*;
import java.util.*;

import jxl.Sheet;
import jxl.Workbook;

import org.openqa.selenium.By;
import org.openqa.selenium.*;
import org.openqa.selenium.*;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.*;


import static junit.framework.Assert.assertTrue;

class User {
    private String username;
    private String url;

    public User(String username, String url) {
        this.username=username;
        this.url=url;
    }

    public String getUsername() {return username;}
    public String getPassword() {return username.substring(4);}
    public String getUrl() {return url;}

    @Override
    public String toString() {
        return username +
                ' ' +
                url;
    }
}

@RunWith(Parameterized.class)
public class Test {
    private static List<User> information;
    private static WebDriver driver;

    private static boolean isInvalid(String obj) {
        if (obj==null) return true;
        if ("".equals(obj.trim())) return true;
        return false;
    }

    private static boolean check(String ori, String obj) {
        if (ori==null || obj==null) return false;

        String x=ori.trim();
        String y=obj.trim();

        if (x.equals(y)) return true;
        return false;
    }

    public static List<User> readFile(String path) {
        List<User> re=new ArrayList<>();
        try {
            Workbook workbook=Workbook.getWorkbook(new File(path));
            Sheet sheet=workbook.getSheet(0);

            for (int i=0; i<sheet.getRows(); ++i) {
                String id=sheet.getCell(0,i).getContents();
                String url=sheet.getCell(1,i).getContents();
                re.add(new User(id,url));
            }

            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return re;
    }

    private String username;
    private String password;
    private String url;

    public Test(String username, String password, String expected) {
        this.username=username;
        this.password=password;
        this.url=expected;
    }

    @BeforeClass
    public static void init() {
        String driver_path = System.getProperty("user.dir")+"/IEDriverServer.exe";
        System.setProperty("webdriver.ie.driver", driver_path);

        DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
        ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);

        driver = new InternetExplorerDriver(ieCapabilities);
        //driver.manage().timeouts().implicitlyWait(15000, TimeUnit.MILLISECONDS);
    }

    @org.junit.Test
    public void setUp() {
        driver.get("https://psych.liebes.top/st");

        WebElement username_element=driver.findElement(By.id("username"));
        WebElement password_element=driver.findElement(By.id("password"));
        WebElement submit=driver.findElement(By.id("submitButton"));

        assertTrue(!isInvalid(url));

        username_element.sendKeys(username);
        password_element.sendKeys(password);
        submit.click();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}

        assertTrue(check(driver.findElement(By.xpath("//p[@class='login-box-msg']")).getText(),url));
    }

    @AfterClass
    public static void cleanup() {
        driver.close();

//        int equal_count=0,almost_equal_count=0,unequal_count=0,error_count=0;
//        for (Status x : result) {
//            if (x.getStatus()==Status.equal) ++equal_count;
//            else if (x.getStatus()==Status.almost_equal) ++almost_equal_count;
//            else if (x.getStatus()==Status.unequal) ++unequal_count;
//            else ++error_count;
//        }
//
//        int total=equal_count+almost_equal_count+unequal_count+error_count;
//        System.out.println("Total count:"+total);
//        System.out.println("Equal count:"+equal_count);
//        System.out.println("Almost equal count:"+almost_equal_count);
//        System.out.println("Unequal count:"+unequal_count);
//        System.out.println("Error count:"+error_count);
    }

    @Parameters
    public static Collection<Object[]> provide() {
    	information=readFile(System.getProperty("user.dir")+"/input.xls");
        Collection<Object[]> re=new ArrayList<>();
        for (User x : information) {
            re.add(new Object[]{x.getUsername(),x.getPassword(),x.getUrl()});
        }
        return re;
    }
}