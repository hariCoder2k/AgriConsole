import java.util.*;
import java.sql.*;
class Admin{
    private Statement stmt;
    public Admin(Statement st)
    {
        this.stmt = st;
    }
    public int addProduct(Product pro) throws Exception
    {
        String sql= "INSERT INTO product VALUES ("+pro.getId()+",'"+pro.getName()+"', '"+pro.getType()+"',"+pro.getPrice()+","+pro.getQuantity()+",'"+pro.getExpDate()+"')";
        int rowsAffected = stmt.executeUpdate(sql);
        return rowsAffected;
    }
    public int removeProduct(int id)throws Exception{
        String sql = "DELETE FROM product where id="+id;
        int rowsAffected = stmt.executeUpdate(sql);
        return rowsAffected;
    }
}

class LineItem {
    private int id;
    private int count;
    private String name;
    private float price;

    void setValue(int id, int count) {
        this.id = id;
        this.count = count;
        try {
            DBconnection.createConnection();
            Connection con=DBconnection.getDbConnection();
            Statement stmt = con.createStatement();
            ResultSet res = stmt.executeQuery("select price from product where id=" + id);
            res.next();
            this.price = Float.parseFloat(res.getString(1)) * this.count;
            res = stmt.executeQuery("select name from product where id=" + this.id);
            res.next();
            this.name = res.getString(1);
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }

    int getId() {
        return this.id;
    }

    int getCount() {
        return this.count;
    }

    float getPrice() {
        return this.price;
    }

    String getName() {
        return this.name;
    }
}
class Customer{
    public int makeOrder(ArrayList<LineItem> items)throws Exception{
        int amount=0,oid;
        DBconnection.createConnection();
        Connection con=DBconnection.getDbConnection();
        Statement statement = con.createStatement();
        ResultSet resultSet;
        for (LineItem iter : items) {
            amount += iter.getPrice();
            statement.executeUpdate("update product set quantity=quantity-"+iter.getCount()+" where id="+iter.getId());
        }
        resultSet = statement.executeQuery("select * from orders;");
        if(!resultSet.next()){
            oid = 1;
        }
        else{
            resultSet = statement.executeQuery("select max(id) as oid from orders;");
            resultSet.next();
            oid = resultSet.getInt("oid")+1;
        }
        System.out.println("Total amount for order is "+amount);
        int rowcount = statement.executeUpdate("insert into orders values("+oid+",current_date(),"+amount+","+1+")");
        return rowcount;
    }
    public Boolean checkUser(int id) throws Exception
    {
        DBconnection.createConnection();
        Connection con=DBconnection.getDbConnection();
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from customer where id="+id);
        if(!resultSet.next())
        {
            return false;
        }
        else{
            return true;
        }
    }
    public ResultSet listMyOrders(int id) throws Exception{
        DBconnection.createConnection();
        Connection con=DBconnection.getDbConnection();
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from orders where cid="+id);
        return resultSet;
    }
}
class Order{
    public int cancelOrder(int oid)throws Exception
    {
        int rowsAffected=0;
        DBconnection.createConnection();
        Connection con=DBconnection.getDbConnection();
        Statement stmt = con.createStatement();
        String sql = "DELETE FROM orders where id="+oid;
        rowsAffected = stmt.executeUpdate(sql);
        return rowsAffected;
    }
    public boolean checkOrder(int id)throws Exception
    {
        DBconnection.createConnection();
        Connection con=DBconnection.getDbConnection();
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from orders where id="+id);
        if(!resultSet.next())
        {
            return false;
        }
        else{
            return true;
        }   
    }
    public void  makePayment(int oid)throws Exception
     {
        int amount = 0;
        Scanner in = new Scanner(System.in);
        DBconnection.createConnection();
        Connection con=DBconnection.getDbConnection();
        Statement statement = con.createStatement();
        System.out.println("Enter cardno");
        String cardno = in.nextLine();
        System.out.println("Enter cvv:");
        String cvv = in.nextLine();
        ResultSet resultSet = statement.executeQuery("select * from card where cardno='"+cardno+"' and cvv='"+cvv+"';");
        resultSet.next();
        int cid= resultSet.getInt("cid");
        System.out.println("cid"+cid);
        resultSet = statement.executeQuery("select amount from orders where id="+oid); 
        resultSet.next();
        amount = resultSet.getInt("amount");
        System.out.println("You amount is "+amount);
        System.out.println("Beginning Transaction");
        statement.executeUpdate("update card set amount="+"amount-"+amount+" where id="+cid);
        statement.executeUpdate("update card set amount="+"amount+"+amount+" where id="+2);
        System.out.println("Transaction completed");
        in.close();
     }
}
class Product{
    private int id;
    private String name;
    private String type;
    private int price;
    private int quantity;
    private String exp;

    public void setDetails(int id,int price,int qty,String name,String typ,String ex){
        this.id = id;
        this.price = price;
        this.quantity = qty;
        this.name = name;
        this.type = typ;
        this.exp = ex;
    }
    static Boolean isAvailable(int id, int count)throws Exception {

            DBconnection.createConnection();
            Connection con=DBconnection.getDbConnection();
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("select quantity from product where id=" + id);
            if (resultSet.next() && Integer.parseInt(resultSet.getString(1)) >= count) {
                return true;
            } else {
                return false;
            }
    }
    public int getId()
    {
        return this.id;
    }
    public int getPrice()
    {
        return this.price;
    }
    public int getQuantity(){
        return this.quantity;
    }
    public String getName()
    {
        return this.name;
    }
    public String getType()
    {
        return this.type;
    }
    public String getExpDate(){
        return this.exp;
    }
    public  static ResultSet getProducts(Statement stmt) throws Exception
    {
        ResultSet rse;
        rse = stmt.executeQuery("select id,name,type,price,quantity,exp from product");
        return rse;
    }

    public static ResultSet getProductByName(Statement stmt,String pattern) throws Exception{
        ResultSet rse;
        rse = stmt.executeQuery("select id,name,type,price,exp from product where name LIKE '"+pattern+"'");
        return rse;
    }

    public static ResultSet getProductByType(Statement stmt,String pattern) throws Exception{
        ResultSet rse;
        rse = stmt.executeQuery("select id,name,type,price,exp from product where type LIKE '"+pattern+"'");
        return rse;
    }


}
class DBconnection{
    private static Connection connection;
    private static final String dbDriver = "com.mysql.cj.jdbc.Driver";
    private static final String dbUrl = "jdbc:mysql://localhost:3306/agri";
    private static final String user = "root";
    private static final String passwd = "";
    public static void createConnection(){
        try{
            Class.forName(dbDriver);
            connection =  DriverManager.getConnection(dbUrl,user,passwd);
        }catch(Exception e){
            System.out.println(e);
        }
    }
    public static Connection getDbConnection(){return connection;}
}
public class Agri
{
    public static void main(String[] args)
    {
        try
        {
            DBconnection.createConnection();
            Connection con=DBconnection.getDbConnection();
            Statement stmt = con.createStatement();
            ResultSet rs;
            Admin ad = new Admin(stmt);
            Customer customer =  new Customer();
            Order order = new Order();
            if(con!=null)
            {
                System.out.println("Connected");
            }
            int role;
            Scanner in = new Scanner(System.in);
            while(true)
            {
                System.out.println("Enter roles:(0:admin,1:customer,-1:exit)`");
                role = Integer.parseInt(in.nextLine());;
                if(role >=0 && role<=1)
                {
                    String c = (role == 0)?"admin":"customer";
                    System.out.println("The selected role is "+c );
                    if(role == 0)
                    {
                                    System.out.println("Welcome to admin console");
                                    int ch=1;
                                    while(ch != 0)
                                    {
                                            System.out.println("Enter your choice(1 to view Menu):");
                                            ch = Integer.parseInt(in.nextLine());;

                                            switch(ch){
                                                    case 1:{
                                                                    System.out.println("You have selected Menu");
                                                                    System.out.println("1.MENU");
                                                                    System.out.println("2.Add product");
                                                                    System.out.println("3.Remove product");
                                                                    System.out.println("4.View products");
                                                                    System.out.println("5.Exit console");
                                                                    break;
                                                    }
                                                    case 2:{
                                                            Product p = new Product();
                                                            System.out.println("You have selected Add Product");
                                                            System.out.println("Enter product id:");
                                                            int id = Integer.parseInt(in.nextLine());;
                                                            System.out.println("Enter product name:");
                                                            String name = in.nextLine();
                                                            System.out.println("Enter product type: ");
                                                            String type = in.nextLine();
                                                            System.out.println("Enter product quantity:");
                                                            int qty = Integer.parseInt(in.nextLine());
                                                            System.out.println("Enter product price:");
                                                            int price = Integer.parseInt(in.nextLine());
                                                            System.out.println("Enter product expiry date:");
                                                            String exp = in.nextLine();
                                                            p.setDetails(id, price, qty, name, type,exp);
                                                            int rowsAffected = ad.addProduct(p);
                                                            if (rowsAffected == 1) {
                                                                System.out.println("Product added!");
                                                            } else {
                                                                System.out.println("Product not added!");
                                                        }
                                                            break;
                                                    }
                                                    case 3:{
                                                            System.out.println("You have selected Remove Product");
                                                            System.out.println("Enter product id:");
                                                            int id = Integer.parseInt(in.nextLine());
                                                            int rowsaffected = ad.removeProduct(id);
                                                            if(rowsaffected >= 1)
                                                            {
                                                                System.out.println("Product "+id+ " deleted successfully");
                                                            }
                                                            else{
                                                                System.out.println("Product "+id+ " deleted failed");
                                                            }
                                                            break;
                                                    }
                                                    case 4:{
                                                            System.out.println("You have selected View Products");
                                                            rs = Product.getProducts(stmt);
                                                            System.out.println("####################################");
                                                            System.out.println("PRODUCT LIST\n");
                                                            System.out.println("ID\tNAME\tTYPE\t\tQTY\t\tPRICE\tEXPIRY DATE");
                                                            while(rs.next())
                                                            {
                                                                System.out.println(rs.getInt("id")+"\t"+rs.getString("name")+"\t"+rs.getString("type")+"\t"+rs.getInt("quantity")+"\t"+"\t"+rs.getInt("price")+"\t"+rs.getString("exp"));
                                                            }
                                                            System.out.println("####################################");
                                                            break;
                                                    }
                                                    case 5:{
                                                                ch=0;
                                                                System.out.println("Exiting console .....");
                                                                break;
                                                    }
                                                    default:
                                                            {
                                                                    System.out.println("Invalid");
                                                            }
                                            }
                                    }
                    }
                    else
                    {
                            System.out.println("Welcome to customer console");
                            int ch=1;
                            while(ch != 0)
                            {
                                    System.out.println("Enter your choice(1 to view Menu):");
                                    ch = Integer.parseInt(in.nextLine());;
                                    switch(ch){
                                            case 1:{
                                                            System.out.println("You have selected Menu");
                                                            System.out.println("1.MENU");
                                                            System.out.println("2.List all products");
                                                            System.out.println("3.Search products");
                                                            System.out.println("4.Order product");
                                                            System.out.println("5.Cancel Order");
                                                            System.out.println("6.Make Payment");
                                                            System.out.println("7.Exit console");
                                                            break;
                                            }
                                            case 2:{
                                                System.out.println("You have selected List all Product");
                                                rs = Product.getProducts(stmt);
                                                System.out.println("####################################");
                                                System.out.println("PRODUCT LIST\n");
                                                System.out.println("ID\tNAME\tTYPE\t\tPRICE\tEXPIRY DATE");
                                                while(rs.next())
                                                {
                                                    System.out.println(rs.getInt("id")+"\t"+rs.getString("name")+"\t"+rs.getString("type")+"\t"+rs.getInt("price")+"\t"+rs.getString("exp"));
                                                }
                                                System.out.println("####################################");
                                                break;
                                            }
                                            case 3:{
                                                String pattern;
                                                System.out.println("You have selected Search Product");
                                                System.out.println("1.Search by name");
                                                System.out.println("2.Search by type");
                                                System.out.println("Enter your choice:");
                                                int choice = Integer.parseInt(in.nextLine());
                                                if(choice == 1)
                                                {
                                                    System.out.println("Enter product name:");
                                                    pattern = in.nextLine();
                                                    rs = Product.getProductByName(stmt,pattern);
                                                    System.out.println("####################################");
                                                    System.out.println("PRODUCT LIST\n");
                                                    System.out.println("ID\tNAME\tTYPE\t\tPRICE\tEXPIRY DATE");
                                                    while(rs.next())
                                                    {
                                                        System.out.println(rs.getInt("id")+"\t"+rs.getString("name")+"\t"+rs.getString("type")+"\t"+rs.getInt("price")+"\t"+rs.getString("exp"));
                                                    }
                                                    System.out.println("####################################");
                                                }
                                                else if(choice == 2){
                                                    System.out.println("Enter type:");
                                                    pattern = in.nextLine();
                                                    rs = Product.getProductByType(stmt,pattern) ;
                                                    System.out.println("####################################");
                                                    System.out.println("PRODUCT LIST\n");
                                                    System.out.println("ID\tNAME\tTYPE\t\tPRICE\t\tEXPIRY DATE");
                                                    while(rs.next())
                                                    {
                                                        System.out.println(rs.getInt("id")+"\t"+rs.getString("name")+"\t"+rs.getString("type")+"\t"+rs.getInt("price")+"\t"+rs.getString("exp"));
                                                    }
                                                    System.out.println("####################################");  
                                                }
                                                break;
                                            }
                                            case 4:{
                                                System.out.println("You have selected Order Product\n");
                                                System.out.println("Enter customer id:");
                                                int customerid = Integer.parseInt(in.nextLine());
                                                if(!customer.checkUser(customerid)){
                                                    System.out.println("Invalid User");
                                                    break;
                                                }
                                                System.out.print("Product List available for sale");
                                                rs = stmt.executeQuery("select id,name,type,price,exp from product");
                                                System.out.println("\n");
                                                while(rs.next())
                                                {
                                                    System.out.println(rs.getInt("id")+"\t"+rs.getString("name")+"\t"+rs.getString("type")+"\t"+rs.getInt("price")+"\t"+rs.getString("exp"));
                                                }
                                                int don = 0;
                                                ArrayList<LineItem> producItems = new ArrayList<>();
                                                while(don != 1){
                                                    System.out.println("1.select a item by id: ");
                                                    int selid = Integer.parseInt(in.nextLine());
                                                    System.out.println("select qty: ");
                                                    int n = Integer.parseInt(in.nextLine());
                                                    if(Product.isAvailable(selid, n)){
                                                        LineItem l = new LineItem();
                                                        l.setValue(selid, n);
                                                        producItems.add(l);
                                                    }
                                                    else{
                                                        System.out.println("Not Enough quantity try again");
                                                    }
                                                    System.out.println("Done ordering?(1/0)");
                                                    don = Integer.parseInt(in.nextLine());
                                                    }
                                                    System.out.println("Order List");
                                                    for (LineItem lineItem : producItems) {
                                                        System.out.println(lineItem.getId()+"\t"+lineItem.getName()+"\t"+lineItem.getPrice()+"\t"+lineItem.getCount());
                                                    }
                                                    int ro = customer.makeOrder(producItems);
                                                    if(ro == 1)
                                                    {
                                                        System.out.println("Order placed successfully");
                                                    }
                                                    else{
                                                        System.out.println("Order failed");
                                                    }
                                                break;        
                                            }
                                            case 5:{
                                                System.out.println("You have selected cancel Order");
                                                System.out.println("Enter customer id:");
                                                int customerid = Integer.parseInt(in.nextLine());
                                                if(customer.checkUser(customerid))
                                                {
                                                    rs = customer.listMyOrders(customerid);
                                                    System.out.println("My Orders");
                                                    System.out.println("OrderId\tOrderDate\tamount\t\t");
                                                    while(rs.next()){
                                                    System.out.println(rs.getInt("id")+"\t"+rs.getDate("odate")+"\t"+rs.getInt("amount"));
                                                    }
                                                    System.out.println("Enter order id to be cancelled:");
                                                    int oid = Integer.parseInt(in.nextLine());
                                                    int rc = order.cancelOrder(oid);
                                                    if( rc == 1)
                                                    {
                                                        System.out.println("Order cancelled ");
                                                    }
                                                    else{
                                                        System.out.println("Order cancelled");
                                                    }
                                                }
                                                break;
                                            }
                                            case 6:{
                                                System.out.println("You have selected make payment");
                                                System.out.println("Enter order id");
                                                int oid = Integer.parseInt(in.nextLine());
                                                if(order.checkOrder(oid)){
                                                    System.out.println("Valid Order");
                                                    order.makePayment(oid);
                                                }
                                                else{
                                                    System.out.println("Invalid");
                                                }
                                                break;
                                            }
                                            case 7:{
                                                ch=0;
                                                System.out.println("Exiting console .....");
                                                break;
                                            }
                                            default:
                                            {
                                                        System.out.println("Invalid choice");
                                            }
                                    }

                            }
                        }
                    }

                        else if(role != -1){
                                    System.out.println("Invalid role try again");
                        }
                        if(role == -1)
                        {
                                    System.out.println("Exiting ... ");
                                break;
                        }
                        
                    }
                  in.close();  
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}
