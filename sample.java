import java.util.*;

public class sample
{
        public static void main(String... args)
        {
                int role;
                Scanner in = new Scanner(System.in);
                while(true){
                System.out.println("Enter roles:(0:admin,1:customer,-1:exit)`");
                role = in.nextInt();
                if(role >=0 && role<=1){
                        String c = (role == 0)?"admin":"customer";
                        System.out.println("The selected role is "+c );
                        if(role == 0){
                                System.out.println("Welcome to admin console");
                                int ch=1;
                                while(ch != 0){
                                        System.out.println("Enter your choice(1 to view Menu):");
                                        ch = in.nextInt();
                                        switch(ch){
                                                case 1:{
                                                               System.out.println("You have selected Menu");
                                                               System.out.println("1.MENU");
                                                               System.out.println("2.Add product");
                                                               System.out.println("3.Remove product");
                                                               System.out.println("4.Viewe products");
                                                               System.out.println("5.Exit console");
                                                               break;
                                                }
                                                case 2:{
                                                        System.out.println("You have selected Add Product");
                                                        System.out.println("Enter product name:");
                                                        String name = in.nextLine();
                                                        break;
                                                }
                                                case 3:{
                                                         System.out.println("You have selected Remove Product");
                                                        break;
                                                }
                                                case 4:{
                                                        System.out.println("You have selected View Products");
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
                        else{
                                System.out.println("Welcome to customer console");
                                int ch=1;
                                while(ch != 0){
                                        System.out.println("Enter your choice(1 to view Menu):");
                                        ch = in.nextInt();
                                        switch(ch){
                                                case 1:{
                                                               System.out.println("You have selected Menu");
                                                               System.out.println("1.MENU");
                                                               System.out.println("2.Order product");
                                                               System.out.println("3.Make Paymentt");
                                                               System.out.println("4.Exit console");
                                                               break;
                                                }
                                                case 2:{
                                                        System.out.println("You have selected Order Product");
                                                        break;
                                                }
                                                case 3:{
                                                         System.out.println("You have selected Make Payment");
                                                        break;
                                                }
                                                case 4:{
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
                if(role == -1){
                         System.out.println("Exiting ... ");
                        break;
                }
                }
        }
}