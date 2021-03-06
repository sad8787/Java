package Server;
import User.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

public class Server {
    public static void main(String[] args) {
        {
            ArrayList<User> users = new ArrayList<User>();
            try {
                ServerSocket serverSocket = new ServerSocket(8188); // Создаём серверный сокет
                System.out.println("Сервер запущен");
                while (true){ // Бесконечный цикл для ожидания родключения клиентов
                    Socket socket = serverSocket.accept(); // Ожидаем подключения клиента
                    System.out.println("Клиент подключился");
                    User currentUser = new User(socket);
                    //users.add(currentUser);
                    DataInputStream in = new DataInputStream(currentUser.getSocket().getInputStream()); // Поток ввода
                    DataOutputStream out = new DataOutputStream(currentUser.getSocket().getOutputStream()); // Поток вывода
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                out.writeUTF("Добро пожаловать на сервер");
                                String userName ; // Ожидаем имя от клиента
                                boolean exists=true;
                                do {
                                    out.writeUTF("Введите ваше имя: ");
                                    userName = in.readUTF();
                                    if (NameExists( users,userName)){
                                        out.writeUTF("Это имя уже используется ");
                                    }
                                    else{
                                        exists=false;
                                        break;
                                    }
                                }while (exists);
                                currentUser.setUserName(userName);
                                users.add(currentUser);
                                out.writeUTF("Ваше имя: "+currentUser.getUserName());
                                for (User user : users) {
                                    DataOutputStream out = new DataOutputStream(user.getSocket().getOutputStream());
                                    out.writeUTF(currentUser.getUserName()+" присоединился к беседе");
                                }
                                while (true){
                                    String request = in.readUTF(); // Ждём сообщение от пользователя
                                    System.out.println(currentUser.getUserName()+": "+request);
                                    //чтобы отправить личное сообщение, вы должны написать правильную структуру
                                    //Рене: sssyysy sudoisoa
                                    //где  "user name ->"  разделитель
                                    String [] requestarray=request.split("->");//
                                    if ( NameExists( users,requestarray[0])&& request.contains("->"))
                                    {
                                        for (User user : users) {
                                            if(users.indexOf(user) == users.indexOf(currentUser)) continue;
                                            if (requestarray[0].compareTo(user.getUserName())==0 ){
                                                DataOutputStream out = new DataOutputStream(user.getSocket().getOutputStream());
                                                out.writeUTF(currentUser.getUserName()+": "+request);
                                            }
                                        }
                                    }else{
                                        for (User user : users) {
                                            if(users.indexOf(user) == users.indexOf(currentUser)) continue;
                                            DataOutputStream out = new DataOutputStream(user.getSocket().getOutputStream());
                                            out.writeUTF(currentUser.getUserName()+": "+request);
                                        }
                                    }

                                }
                            }catch (IOException e){
                                users.remove(currentUser);
                                for (User user : users) {
                                    try {
                                        DataOutputStream out = new DataOutputStream(user.getSocket().getOutputStream());
                                        out.writeUTF(currentUser.getUserName()+" покинул чат");
                                    } catch (IOException ioException) {
                                        ioException.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                    thread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static boolean NameExists(ArrayList<User> users,String name){
        boolean result=false;
        for (User user : users){
            if (name.compareTo(user.getUserName())==0 ){
                result= true;
                break;
            }
        }
        return result;
    }
}