package com.cj.testdbstruct;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cj.testdbstruct.db.BaseDao;
import com.cj.testdbstruct.db.BaseDaoFactory;
import com.cj.testdbstruct.entity.A;
import com.cj.testdbstruct.entity.User;
import com.cj.testdbstruct.sub_db.UserDao;

import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void insert(View view) {
//        A where = new A();
//        where.setId(444);
//        BaseDaoFactory.getInstance().getDao(BaseDao.class, A.class)
//                .insert(new A(444, "sad"));
        BaseDaoFactory.getInstance().getDao(UserDao.class, User.class)
                .login(new User(1, "cj", "aaa"));
        Toast.makeText(this, "完成插入", Toast.LENGTH_SHORT).show();
    }

    public void query(View view) {
        A where = new A();
        where.setId(444);
        List<A> res = BaseDaoFactory.getInstance().getDao(BaseDao.class, A.class)
                .query(where);
        for (A a : res) {
            Log.i(TAG, "query: " + a.toString());
        }
    }
}
