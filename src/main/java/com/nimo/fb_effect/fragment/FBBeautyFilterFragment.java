package com.nimo.fb_effect.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.adapter.FBBeautyFilterItemViewBinder;
import com.nimo.fb_effect.base.FBBaseLazyFragment;
import com.nimo.fb_effect.model.FBEventAction;
import com.nimo.fb_effect.model.FBViewState;
import com.nimo.fb_effect.model.FBBeautyFilterConfig;
import com.nimo.fb_effect.model.FBBeautyFilterConfig.FBBeautyFilter;
import com.nimo.fb_effect.model.FBState;
import com.nimo.fb_effect.utils.FBConfigCallBack;
import com.nimo.fb_effect.utils.FBConfigTools;
import com.nimo.fb_effect.utils.FBUICacheUtils;
import com.nimo.fb_effect.utils.MyItemDecoration;
import java.util.ArrayList;
import java.util.List;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * 风格滤镜
 */
public class FBBeautyFilterFragment extends FBBaseLazyFragment {

  private View container;

  private RecyclerView rvFilter;

  private final List<FBBeautyFilter> items = new ArrayList<>();

  @Override protected int getLayoutId() {
    return R.layout.fragment_filter;
  }

  private final MultiTypeAdapter listAdapter = new MultiTypeAdapter();

  @Override protected void initView(View view, Bundle savedInstanceState) {

    container = findViewById(R.id.container);
    rvFilter = findViewById(R.id.rv_filter);

    rvFilter.addItemDecoration(new
        MyItemDecoration(5)
    );

    changeTheme(null);
    items.clear();

    FBBeautyFilterConfig filterList = FBConfigTools.getInstance().getBeautyFilterConfig();

    if (filterList == null) {
      FBConfigTools.getInstance().getStyleFiltersConfig(new FBConfigCallBack<List<FBBeautyFilter>>() {
        @Override public void success(List<FBBeautyFilterConfig.FBBeautyFilter> list) {
          items.addAll(list);
          initRecyclerView();
        }

        @Override public void fail(Exception error) {
          error.printStackTrace();
          Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        }
      });
    } else {
      items.addAll(filterList.getFilters());
      initRecyclerView();
    }

  }

  public void initRecyclerView() {
    listAdapter.register(FBBeautyFilter.class, new FBBeautyFilterItemViewBinder());
    listAdapter.setItems(items);
    rvFilter.setAdapter(listAdapter);
    rvFilter.smoothScrollToPosition(FBUICacheUtils.getBeautyFilterPosition());
    //sync progress
    RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");
  }

  /**
   * 更换主题
   */
  @Subscribe(thread = EventThread.MAIN_THREAD,
             tags = { @Tag(FBEventAction.ACTION_CHANGE_THEME) })
  public void changeTheme(@Nullable Object o) {
    Log.e("切换主题:", FBState.isDark ? "黑色" : "白色");
    if (FBState.isDark) {
       container.setBackground(ContextCompat.getDrawable(getContext(),
           R.color.dark_background));
    } else {
       container.setBackground(ContextCompat.getDrawable(getContext(),
           R.color.light_background));
    }
  }


  @Override protected void onFragmentStartLazy() {
    super.onFragmentStartLazy();
    //更新ui状态
    FBState.currentSecondViewState = FBViewState.FILTER;
    FBState.currentSliderViewState = FBViewState.FILTER_STYLE;
    //同步滑动条
    RxBus.get().post(FBEventAction.ACTION_SYNC_PROGRESS, "");
    listAdapter.notifyDataSetChanged();
  }

}
