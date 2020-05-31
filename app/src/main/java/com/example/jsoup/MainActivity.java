package com.example.jsoup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.EventLogTags;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<ItemObject> list = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        //AsyncTask 작동시킴(파싱)
        new Description().execute();

        //샘플 html 코드 제시
        String html = "<html><head><title>첫번째 에제입니다.</title></head>"
                + "<body><h1>테스트</h1><p>간단히 HTML을 파싱해 보는 샘플예제입니다.</p></body></html>";

        Document doc = Jsoup.parse(html);

        Elements title = doc.select("title");
        Log.v("result: ", "doc= "+ doc);
        Log.v("result: ", "title= " + title);

    }
    private class Description extends AsyncTask<Void, Void, Void> {

        //진행바표시
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //진행다일로그 시작
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("잠시 기다려 주세요.");
            progressDialog.show();
            Log.v("result: ", "잠시 기달 ");

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.v("result: ", "잠시 기달 2");

                Document doc = Jsoup.connect("https://movie.naver.com/movie/running/current.nhn").get();
                Log.v("result:", "doc " + doc);

                Elements mElementDataSize = doc.select("ul[class=lst_detail_t1]").select("li"); //필요한 녀석만 꼬집어서 지정
                int mElementSize = mElementDataSize.size(); //목록이 몇개인지 알아낸다. 그만큼 루프를 돌려야 하나깐.
                Log.v("result:", "mElementDataSize " + mElementDataSize);
                Log.v("result:", "mElementSize " + mElementSize);

                for(Element elem : mElementDataSize){ //이렇게 요긴한 기능이
                    Log.v("result:", "elem" + elem);
                    //영화목록 <li> 에서 다시 원하는 데이터를 추출해 낸다.
                    String my_title = elem.select("li dt[class=tit] a").text(); //
                    Log.v("result:", "my_title" + my_title);

                    String my_link = elem.select("li div[class=thumb] a").attr("href");
                    Log.v("result:", "my_link" + my_link);

                    String my_imgUrl = elem.select("li div[class=thumb] a img").attr("src"); //
                    Log.v("result:", "my_imgUrl" + my_imgUrl);

                    //특정하기 힘들다... 저 앞에 있는집의 오른쪽으로 두번째집의 건너집이 바로 우리집이야 하는 식이다.
                    Element rElem = elem.select("dl[class=info_txt1] dt").next().first();
                    Log.v("result:", "rElem" + rElem);

                    String my_release = rElem.select("dd").text(); //
                    Log.v("result:", "my_release" + my_release);

                    Element dElem = elem.select("dt[class=tit_t2]").next().first();
                    Log.v("result:", "dElem" + dElem);

                    String my_director = "감독: " + dElem.select("a").text(); //
                    Log.v("result:", "my_director" + my_director);

                    //Log.d("test", "test" + mTitle);
                    //ArrayList에 계속 추가한다.
                    list.add(new ItemObject(my_title, my_imgUrl, my_link, my_release, my_director));
                }

                //추출한 전체 <li> 출력해 보자.
                Log.v("result:", "List " + mElementDataSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //ArraList를 인자로 해서 어답터와 연결한다.
            MyAdapter myAdapter = new MyAdapter(list);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(myAdapter);

            progressDialog.dismiss();
            Log.v("result: ", "잠시기달 4");

        }
    }
}
