import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.net.*;
import java.io.*;
import java.util.*;

public class Main {

    static class Equipment {
        String name, sport, icon, color;
        int total, available, issued, damaged;

        Equipment(String name, String sport, String icon,
                  String color, int total) {
            this.name = name;
            this.sport = sport;
            this.icon = icon;
            this.color = color;
            this.total = total;
            this.available = total;
            this.issued = 0;
            this.damaged = 0;
        }
    }

    static Map<String, Equipment> items =
        new LinkedHashMap<String, Equipment>();

    static Map<String, Integer> borrowed =
        new HashMap<String, Integer>();

    static {
        add("Cricket Bat", "Cricket", "&#127951;", "#ff9f43", 10);
        add("Football", "Football", "&#9917;", "#54a0ff", 15);
        add("Basketball", "Basketball", "&#127936;", "#ff6b6b", 12);
        add("Volleyball", "Volleyball", "&#127952;", "#1dd1a1", 10);
        add("Badminton Racket", "Badminton", "&#127992;", "#a29bfe", 20);
    }

    static void add(String name, String sport, String icon,
                    String color, int qty) {

        items.put(name.toLowerCase(),
            new Equipment(name, sport, icon, color, qty));
    }

    // HTML escaping
    static String escape(String value) {
        if (value == null) return "";

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }

    // Website page
    static String page() {

        StringBuilder cards = new StringBuilder();

        int total = 0;
        int available = 0;
        int issued = 0;
        int damaged = 0;

        for (Equipment e : items.values()) {

            total += e.total;
            available += e.available;
            issued += e.issued;
            damaged += e.damaged;

            int percentage = 0;

            if (e.total > 0) {
                percentage = e.available * 100 / e.total;
            }

            cards.append("<div class='card' style='--c:")
                 .append(escape(e.color)).append("'>");

            cards.append("<div class='icon'>")
                 .append(e.icon).append("</div>");

            cards.append("<h2>").append(escape(e.name))
                 .append("</h2>");

            cards.append("<p>").append(escape(e.sport))
                 .append(" Equipment</p>");

            cards.append("<div class='details'>");

            cards.append("<span>Total</span><b>")
                 .append(e.total).append("</b>");

            cards.append("<span>Available</span><b>")
                 .append(e.available).append("</b>");

            cards.append("<span>Issued</span><b>")
                 .append(e.issued).append("</b>");

            cards.append("<span>Damaged</span><b>")
                 .append(e.damaged).append("</b>");

            cards.append("</div>");

            cards.append("<div class='bar'>");
            cards.append("<div style='width:")
                 .append(percentage).append("%'></div>");
            cards.append("</div>");

            cards.append("</div>");
        }

        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html><head>");
        html.append("<title>SPORTIFY</title>");

        html.append("<meta name='viewport' ");
        html.append("content='width=device-width, initial-scale=1'>");

        html.append("<style>");

        html.append("*{box-sizing:border-box}");

        html.append("body{margin:0;font-family:Arial,sans-serif;");
        html.append("background:#f3f5fb;color:#222}");

        html.append("header{background:linear-gradient(120deg,");
        html.append("#141e30,#243b55);color:white;padding:28px 7%}");

        html.append("header h1{margin:0;font-size:32px}");
        html.append("header p{color:#d0d8e8}");

        html.append("main{padding:30px 7%}");

        html.append(".stats{display:grid;");
        html.append("grid-template-columns:repeat(auto-fit,");
        html.append("minmax(160px,1fr));gap:18px;margin-bottom:30px}");

        html.append(".stat{background:white;padding:22px;");
        html.append("border-radius:15px;box-shadow:0 5px 18px #0001}");

        html.append(".stat h2{margin:5px 0;color:#3657d5}");

        html.append(".grid{display:grid;");
        html.append("grid-template-columns:repeat(auto-fit,");
        html.append("minmax(220px,1fr));gap:20px}");

        html.append(".card{background:white;padding:22px;");
        html.append("border-radius:18px;border-top:5px solid var(--c);");
        html.append("box-shadow:0 5px 18px #0001;transition:.2s}");

        html.append(".card:hover{transform:translateY(-5px)}");
        html.append(".icon{font-size:48px}");
        html.append(".card h2{margin-bottom:5px}");
        html.append(".card p{color:#888}");

        html.append(".details{display:grid;");
        html.append("grid-template-columns:1fr 1fr;");
        html.append("gap:12px;margin-top:20px}");

        html.append(".details span{color:#777}");
        html.append(".details b{text-align:right}");

        html.append(".bar{height:8px;background:#eee;");
        html.append("border-radius:10px;margin-top:20px;overflow:hidden}");

        html.append(".bar div{background:var(--c);height:100%}");

        html.append(".actions{background:white;padding:25px;");
        html.append("border-radius:18px;margin-top:30px}");

        html.append("input,select{padding:12px;margin:7px 5px 7px 0;");
        html.append("border:1px solid #ddd;border-radius:8px}");

        html.append("button{padding:12px 20px;border:0;");
        html.append("border-radius:8px;background:#3657d5;");
        html.append("color:white;cursor:pointer;margin:5px;");
        html.append("font-weight:bold}");

        html.append("button:hover{background:#243b99}");

        html.append(".message{background:#e7f8ed;padding:12px;");
        html.append("margin:15px 0;border-radius:8px;color:#16783a}");

        html.append("@media(max-width:600px){main{padding:20px 5%}}");

        html.append("</style></head><body>");

        html.append("<header>");
        html.append("<h1>&#127942; SPORTIFY</h1>");
        html.append("<p>College Sports Equipment Management System</p>");
        html.append("<h3>Manage &bull; Track &bull; Organize</h3>");
        html.append("</header>");

        html.append("<main>");

        html.append("<h2>Dashboard Overview</h2>");

        html.append("<div class='stats'>");

        html.append("<div class='stat'><p>Total Equipment</p>");
        html.append("<h2>").append(total).append("</h2></div>");

        html.append("<div class='stat'><p>Available</p>");
        html.append("<h2>").append(available).append("</h2></div>");

        html.append("<div class='stat'><p>Issued</p>");
        html.append("<h2>").append(issued).append("</h2></div>");

        html.append("<div class='stat'><p>Damaged</p>");
        html.append("<h2>").append(damaged).append("</h2></div>");

        html.append("</div>");

        html.append("<h2>Sports Equipment</h2>");

        html.append("<div class='grid'>");
        html.append(cards.toString());
        html.append("</div>");

        html.append("<div class='actions'>");

        html.append("<h2>&#9881; Equipment Management</h2>");

        // Add Equipment Form
        html.append("<form method='GET'>");

        html.append("<input type='hidden' name='action' value='add'>");

        html.append("<h3>Add Equipment</h3>");

        html.append("<input name='name' placeholder='Equipment Name' required>");

        html.append("<input name='sport' placeholder='Sport Name' required>");

        html.append("<input name='qty' type='number' min='1' ");
        html.append("placeholder='Quantity' required>");

        html.append("<button type='submit'>Add Equipment</button>");

        html.append("</form>");

        html.append("<hr>");

        // Issue / Return / Damage Form
        html.append("<form method='GET'>");

        html.append("<h3>Issue / Return / Damage Equipment</h3>");

        html.append("<select name='action'>");
        html.append("<option value='issue'>Issue</option>");
        html.append("<option value='return'>Return</option>");
        html.append("<option value='damage'>Mark Damaged</option>");
        html.append("</select>");

        html.append("<select name='name'>");
        html.append(options());
        html.append("</select>");

        html.append("<input name='student' placeholder='Student Name'>");

        html.append("<input name='qty' type='number' min='1' ");
        html.append("placeholder='Quantity' required>");

        html.append("<button type='submit'>Submit</button>");

        html.append("</form>");

        html.append("</div>");
        html.append("</main></body></html>");

        return html.toString();
    }

    // Equipment dropdown
    static String options() {

        StringBuilder s = new StringBuilder();

        for (Equipment e : items.values()) {

            s.append("<option value='")
             .append(escape(e.name))
             .append("'>")
             .append(escape(e.name))
             .append("</option>");
        }

        return s.toString();
    }

    // Process equipment operations
    static String process(Map<String, String> p) {

        String action = p.get("action");
        String name = p.get("name");

        if (action == null) {
            return "Invalid action";
        }

        if (action.equals("add")) {

            try {

                if (name == null || name.trim().length() == 0) {
                    return "Enter equipment name";
                }

                String sport = p.get("sport");

                if (sport == null || sport.trim().length() == 0) {
                    return "Enter sport name";
                }

                int qty = Integer.parseInt(p.get("qty"));

                if (qty <= 0) {
                    return "Quantity must be positive";
                }

                String key = name.trim().toLowerCase();

                if (items.containsKey(key)) {

                    Equipment e = items.get(key);

                    e.total += qty;
                    e.available += qty;

                } else {

                    add(name.trim(), sport.trim(),
                        "&#127942;", "#54a0ff", qty);
                }

                return "Equipment added successfully!";

            } catch (Exception ex) {

                return "Invalid equipment details";
            }
        }

        if (name == null) {
            return "Select equipment";
        }

        Equipment e = items.get(name.toLowerCase());

        if (e == null) {
            return "Equipment not found!";
        }

        try {

            int qty = Integer.parseInt(p.get("qty"));

            if (qty <= 0) {
                return "Invalid quantity";
            }

            String student = p.get("student");

            if (student == null) {
                student = "";
            }

            student = student.trim();

            String key = student.toLowerCase() + "|" +
                         name.toLowerCase();

            if (action.equals("issue")) {

                if (student.length() == 0) {
                    return "Enter student name";
                }

                if (qty > e.available) {
                    return "Insufficient stock!";
                }

                e.available -= qty;
                e.issued += qty;

                Integer old = borrowed.get(key);

                if (old == null) {
                    borrowed.put(key, Integer.valueOf(qty));
                } else {
                    borrowed.put(key,
                        Integer.valueOf(old.intValue() + qty));
                }

                return "Equipment issued successfully!";
            }

            if (action.equals("return")) {

                Integer oldValue = borrowed.get(key);

                int old = oldValue == null ? 0 :
                          oldValue.intValue();

                if (student.length() == 0 || qty > old) {
                    return "Invalid student or return quantity!";
                }

                if (old == qty) {
                    borrowed.remove(key);
                } else {
                    borrowed.put(key, Integer.valueOf(old - qty));
                }

                e.available += qty;
                e.issued -= qty;

                return "Equipment returned successfully!";
            }

            if (action.equals("damage")) {

                if (qty > e.available) {
                    return "Insufficient available stock!";
                }

                e.available -= qty;
                e.damaged += qty;

                return "Equipment marked as damaged!";
            }

            return "Invalid action";

        } catch (Exception ex) {

            return "Please enter valid details!";
        }
    }

    // URL parameter parsing
    static Map<String, String> parse(String query)
            throws Exception {

        Map<String, String> map =
            new HashMap<String, String>();

        if (query == null) {
            return map;
        }

        String[] pairs = query.split("&");

        for (int i = 0; i < pairs.length; i++) {

            String[] a = pairs[i].split("=", 2);

            String key = URLDecoder.decode(a[0], "UTF-8");

            String value = "";

            if (a.length > 1) {
                value = URLDecoder.decode(a[1], "UTF-8");
            }

            map.put(key, value);
        }

        return map;
    }

    // Main method
    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
            new InetSocketAddress(8080), 0);

        server.createContext("/", new com.sun.net.httpserver.HttpHandler() {

            public void handle(HttpExchange exchange)
                    throws IOException {

                String message = "";

                try {

                    if ("GET".equals(exchange.getRequestMethod())) {

                        Map<String, String> params =
                            parse(exchange.getRequestURI().getRawQuery());

                        if (params.containsKey("action")) {
                            message = process(params);
                        }
                    }

                } catch (Exception ex) {

                    message = "Something went wrong!";
                }

                String html = page();

                if (message.length() > 0) {

                    html = html.replace(
                        "<main>",
                        "<main><div class='message'>" +
                        escape(message) + "</div>"
                    );
                }

                byte[] data = html.getBytes("UTF-8");

                exchange.getResponseHeaders().set(
                    "Content-Type",
                    "text/html; charset=UTF-8"
                );

                exchange.sendResponseHeaders(200, data.length);

                OutputStream os = exchange.getResponseBody();

                try {
                    os.write(data);
                    os.flush();
                } finally {
                    os.close();
                }
            }
        });

        server.setExecutor(null);
        server.start();

        System.out.println("SPORTIFY Website Started!");
        System.out.println("Open: http://localhost:8080");
    }
}