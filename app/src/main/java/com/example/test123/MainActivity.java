package com.example.test123;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.test123.Entities.User;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public class simpleDialog {
        public simpleDialog(String s) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(cntx);
            dlgAlert.setMessage(s);
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dlgAlert.create().show();
        }
    }


    public class jsonDell extends  AsyncTask<User, Integer, String> {
        @Override
        protected String doInBackground(User... users) {

            try {
                return users[0].delete();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new simpleDialog(s);
            new jsonGet().execute();
        }
    }

    public class jsonGet extends AsyncTask<Integer, Integer, ArrayList<User>> {

        @Override
        protected ArrayList<User> doInBackground(Integer... integers) {
            try {
               return User.getUsersList();
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<User>();
            }
        }


        @Override
        protected void onPostExecute(ArrayList<User> users) {
            super.onPostExecute(users);
            lw.setAdapter(new UserAdapter(cntx, R.layout.userlayout, users));
        }
    }

    public class jsonSend extends AsyncTask<User, Integer, String> {

        @Override
        protected String doInBackground(User... user) {
            try {
                return user[0].addUser();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new simpleDialog(s);
            new jsonGet().execute();
        }
    }

    ArrayList<User> users;
    Context cntx = this;
    ListView lw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button butt = findViewById(R.id.butt_add);
      butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dlgAlert = new Dialog(cntx);
                dlgAlert.setContentView(R.layout.adduserlayout);
                Button button = dlgAlert.findViewById(R.id.userAddButtonOk);
                final EditText login = dlgAlert.findViewById(R.id.addUserLogin);
                final EditText pass = dlgAlert.findViewById(R.id.addUserPass);
                final EditText name = dlgAlert.findViewById(R.id.addUserName);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new jsonSend().execute(new User(0, login.getText().toString(), pass.getText().toString(), name.getText().toString()));
                    }
                });
                dlgAlert.show();

            }
        });


        lw = findViewById(R.id.listview1);

        jsonGet jsonGet = new jsonGet();
        jsonGet.execute();



    }

    public class UserAdapter extends ArrayAdapter<User> {

        public UserAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final User u = getItem(position);
            if(convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.userlayout, null);
            //convertView.setTag(u);
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(cntx);
                    dlgAlert.setMessage("Вы действительно хотите удалить пользователя (" + u.id + ") ?");
                    dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                           new jsonDell().execute(u);
                        }
                    });
                    dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dlgAlert.create().show();
                    return false;
                }
            });
            TextView id = convertView.findViewById(R.id.userId);
            TextView login = convertView.findViewById(R.id.userLogin);
            TextView pass = convertView.findViewById(R.id.userPass);
            TextView name = convertView.findViewById(R.id.userName);
            login.setText("Логин" + getString(R.string.tab) + u.login);
            pass.setText("Пароль" + getString(R.string.tab) + u.pass);
            name.setText("Имя" + getString(R.string.tab) + u.name);
            id.setText("Пользователь" + String.valueOf(u.id));
            return convertView;
        }

    }
}