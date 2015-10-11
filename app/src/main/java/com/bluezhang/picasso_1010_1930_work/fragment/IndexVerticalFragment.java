package com.bluezhang.picasso_1010_1930_work.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.GridView;
import android.widget.ImageView;
import com.bluezhang.picasso_1010_1930_work.R;
import com.bluezhang.picasso_1010_1930_work.adadapterutil.MultiItemCommonAdapter;
import com.bluezhang.picasso_1010_1930_work.adadapterutil.MultiItemTypeSupport;
import com.bluezhang.picasso_1010_1930_work.adadapterutil.ViewHolder;
import com.bluezhang.picasso_1010_1930_work.bean.Movies;
import com.bluezhang.picasso_1010_1930_work.config.Config;
import com.bluezhang.picasso_1010_1930_work.volleyutil.VolleyUtils;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.core.BitmapSize;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: blueZhang
 * DATE:2015/10/10
 * Time: 20:34
 * AppName:Picasso_1010_1930_work
 * PckageName:com.bluezhang.picasso_1010_1930_work.fragment
 */
public class IndexVerticalFragment extends Fragment {
    private Picasso picasso;

    /**
     * 创建工厂类方法返回Fragment
     * @param title
     * @return
     */
    public static IndexVerticalFragment newInstance(String title){
        IndexVerticalFragment f = new IndexVerticalFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title",title);
        f.setArguments(bundle);
        return f;
    }

    private GridView gridView ;
    private String title ;
    private Movies MV;
    private MultiItemCommonAdapter adapter ;
    private List<Movies.ResultsEntity> datas ;
    private String basePath="http://image.tmdb.org/t/p/w185//";

    private BitmapUtils bitmapUtils;

    @ResInject(id = R.anim.translate,type = ResType.Animation)
    private Animation animation;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("title");
        datas = new ArrayList<Movies.ResultsEntity>();
        bitmapUtils = new BitmapUtils(getActivity());
        picasso = Picasso.with(getActivity().getApplication());
        adapter = new MultiItemCommonAdapter<Movies.ResultsEntity>(getActivity(), datas, new MultiItemTypeSupport<Movies.ResultsEntity>() {
            @Override
            public int getLayoutId(int position, Movies.ResultsEntity movies) {
                return R.layout.little_gridview_item;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public int getItemViewType(int postion, Movies.ResultsEntity movies) {
                return 1;
            }
        })

        {
            @Override
            public void convert(ViewHolder holder, Movies.ResultsEntity movies) {
                ImageView imageView = holder.getView(R.id.little_imageview_item);
                String url = basePath+movies.getPoster_path();
               // Log.i("debug",url);
                /**
                 * 写使用工具添加ImageView
                 * 使用的是这个但是不知道为什么就是加载不行
                 * 添加不上图片
                 */
                picasso.load(url).into(imageView);

                /**
                 * 使用的是Volley加载数据
                 * 可以加载的
                 */
               // VolleyUtils.setImage(getActivity(),url,imageView,R.mipmap.ic_launcher,R.mipmap.ic_launcher);


                /**
                 * 使用的是Xutils的工具进行的加载的图片
                 * 可以加载
                 */
//                if (url != null) {
//                    if (imageView != null) {
//                        initBitmapUtils(url,imageView);
//                    }
//                }


            }
        };






    }

    /**
     * 加载数据
     */
    private void loadDatasData() {

        String url = Config.MOVIE_BACK;
        VolleyUtils.getJsonString(url, getActivity(), new VolleyUtils.MyParser<Movies.ResultsEntity>() {
            @Override
            public void getJson(String response) {
                String jsonString = response;
                Gson gson = new Gson();
                MV = gson.fromJson(jsonString,Movies.class);
                datas.addAll(MV.getResults());
                adapter.notifyDataSetChanged();

            }
        });
    }

    /**
     * 初始化默认的图片加载设置
     * */
    private void initBitmapUtils(String url,ImageView imgV){
        bitmapUtils.configDefaultLoadingImage(R.mipmap.ic_launcher);
        bitmapUtils.configDefaultLoadFailedImage(android.R.drawable.ic_delete);
        bitmapUtils.configDefaultBitmapMaxSize(new BitmapSize(250, 250));
        bitmapUtils.configMemoryCacheEnabled(true);
        bitmapUtils.configDefaultAutoRotation(true);
        bitmapUtils.configDefaultImageLoadAnimation(animation);
        bitmapUtils.configDefaultConnectTimeout(5000);
        bitmapUtils.display(imgV, url);

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.index_vertical_fragment_layout, container, false);
        gridView = (GridView) view.findViewById(R.id.index_gridView1);

        gridView.setAdapter(adapter);
        loadDatasData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
