package com.tokyonth.weather.search;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tokyonth.weather.R;
import com.tokyonth.weather.adapter.SearchCityAdapter;
import com.tokyonth.weather.model.CitySelectionModel;
import com.tokyonth.weather.model.bean.City;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends DialogFragment implements
        DialogInterface.OnKeyListener, ViewTreeObserver.OnPreDrawListener, IOnItemClickListener, SearchAnim.AnimListener, View.OnClickListener {

    public static final String TAG = "SearchFragment";

    private ImageView ivSearchBack;
    private ImageView ivSearchClean;
    private EditText etSearchKeyword;
    private RecyclerView rvSearchCity;

    private View viewSearchOutside;
    private View view;
    private SearchAnim mCircularRevealAnim;
    private List<City> foundCityList = new ArrayList<>();

    private CitySelectionModel selectionModel;
    private SearchCityAdapter searchCityAdapter;

    public void setCitySelect(CitySelectionModel selectionModel) {
        this.selectionModel = selectionModel;
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.SearchDialogStyle);
    }

    @Override
    public void onStart() {
        super.onStart();
        initDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_fragment, container, false);
        init();
        return view;
    }

    private void init() {
        ivSearchBack = view.findViewById(R.id.iv_search_back);
        etSearchKeyword = view.findViewById(R.id.et_search_keyword);
        rvSearchCity = view.findViewById(R.id.rv_search_history);
        ivSearchClean = view.findViewById(R.id.iv_search_clean);
        viewSearchOutside = view.findViewById(R.id.view_search_outside);

        //实例化动画效果
        mCircularRevealAnim = new SearchAnim();
        //监听动画
        mCircularRevealAnim.setAnimListener(this);

        getDialog().setOnKeyListener(this);//键盘按键监听
        ivSearchClean.getViewTreeObserver().addOnPreDrawListener(this);//绘制监听

        //初始化recyclerView
        rvSearchCity.setLayoutManager(new LinearLayoutManager(getContext()));//list类型
        searchCityAdapter = new SearchCityAdapter(foundCityList);
        rvSearchCity.setAdapter(searchCityAdapter);

        //监听编辑框文字改变
        etSearchKeyword.addTextChangedListener(new TextWatcherImpl());
        //使输入框获得焦点
        etSearchKeyword.requestFocus();
        //监听点击
        ivSearchBack.setOnClickListener(this);
        viewSearchOutside.setOnClickListener(this);
        ivSearchClean.setOnClickListener(this);
    }

    public void showFragment(FragmentManager fragmentManager, String tag) {
        if (!this.isAdded()) {
            this.show(fragmentManager, tag);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_search_back || view.getId() == R.id.view_search_outside) {
            hideAnim();
        } else if (view.getId() == R.id.iv_search_clean) {
            etSearchKeyword.setText("");
        }
    }

    private void initDialog() {
        Window window = getDialog().getWindow();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 0.98); //DialogSearch的宽
        window.setLayout(width, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.TOP);
        window.setWindowAnimations(R.style.DialogEmptyAnimation);//取消过渡动画 , 使DialogSearch的出现更加平滑
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            hideAnim();
        } else if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
          if (foundCityList.size() == 0) {
              Toast.makeText(getContext(), "无该城市信息", Toast.LENGTH_SHORT).show();
          } else {
              Toast.makeText(getContext(), "需要选择一个城市", Toast.LENGTH_SHORT).show();
          }
        }
        return false;
    }

    /**
     * 监听搜索键绘制时
     */
    @Override
    public boolean onPreDraw() {
        ivSearchClean.getViewTreeObserver().removeOnPreDrawListener(this);
        mCircularRevealAnim.show(view);
        return true;
    }

    /**
     * 搜索框动画隐藏完毕时调用
     */
    @Override
    public void onHideAnimationEnd() {
        etSearchKeyword.setText("");
        dismiss();
    }

    /**
     * 搜索框动画显示完毕时调用
     */
    @Override
    public void onShowAnimationEnd() {
        if (isVisible()) {
            openKeyboard(getContext(), etSearchKeyword);
        }
    }

    @Override
    public void onItemClick(City city) {
        //Toast.makeText(getContext(), city.getCityName(),Toast.LENGTH_SHORT).show();
        closeKeyboard(getContext(), etSearchKeyword);
        selectionModel.isCity(city);
    }

    private class TextWatcherImpl implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String str = charSequence.toString().trim();
            String queriedCity = str + "%";
            foundCityList = DataSupport.where("cityname like ?",queriedCity).find(City.class);
            searchCityAdapter = new SearchCityAdapter(foundCityList);
            rvSearchCity.setAdapter(searchCityAdapter);
            searchCityAdapter.notifyDataSetChanged();
            searchCityAdapter.setOnItemClickListener(SearchFragment.this);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String keyword = editable.toString();
            if (TextUtils.isEmpty(keyword.trim())) {
                foundCityList.clear();
                searchCityAdapter.notifyDataSetChanged();
            }
        }
    }

    private void hideAnim() {
        closeKeyboard(getContext(), etSearchKeyword);
        mCircularRevealAnim.hide(view);
    }

    private void openKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void closeKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

}
