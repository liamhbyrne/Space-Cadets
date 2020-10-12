import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface NameExtractor {
    void startInput();
    void extractID();
    void buildURL();
    String openConnection();
    void go();

}

class SotonNameExtractor implements NameExtractor {

    private String email;
    private String shortID;
    private URL base_url;
    private URL url_page;

    SotonNameExtractor(){
        try {
            this.base_url = new URL("https://www.ecs.soton.ac.uk/people/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void startInput() {
        try {
            InputStreamReader reader = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(reader);
            System.out.print("Enter full email address:\n>>>");
            this.email = br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void extractID() {
        String pattern = "\\A\\S+?(?=@)";
        Pattern reg = Pattern.compile(pattern);

        Matcher m = reg.matcher(email);
        if (m.find()) {
            shortID = m.group(0);
        } else {
            System.out.println("No group found !");
        }

    }

    public void buildURL() {
        try {
            url_page = new URL(base_url, shortID);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    public String openConnection() {
        try {
            URLConnection soton_connection = url_page.openConnection();
            BufferedReader html_reader = new BufferedReader(new InputStreamReader(soton_connection.getInputStream()));
            String html_line;
            while ((html_line = html_reader.readLine()) != null) {
                int index = html_line.indexOf("property=\"name\"");
                if (index != -1) {
                    String crop = html_line.substring(index);
                    return crop.substring(crop.indexOf(">")+1, crop.indexOf("<"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void go() {
        String result = openConnection();
        if (result != null) {
            System.out.println(result);
        } else {
            System.out.println("Name could not be determined");
        }
    }


    public static void main(String[] args) {
        NameExtractor sne = new SotonNameExtractor();
        sne.startInput();
        sne.extractID();
        sne.buildURL();
        sne.go();
    }
}
