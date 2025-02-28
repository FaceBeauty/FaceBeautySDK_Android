package com.nimo.fb_effect.adapter;

import android.os.Handler;
import androidx.annotation.NonNull;;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;
import com.liulishuo.okdownload.core.listener.DownloadListener2;
import com.nimo.fb_effect.R;
import com.nimo.fb_effect.model.FBMaskResEnum;
import com.nimo.fb_effect.model.FBDownloadState;
import com.nimo.fb_effect.model.FBMaskConfig;
import com.nimo.fb_effect.model.FBMaskConfig.FBMask;
import com.nimo.fb_effect.utils.FBSelectedPosition;
import com.nimo.fb_effect.utils.FBUnZip;
import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBItemEnum;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 贴纸Item的适配器
 */
public class FBMaskAdapter extends RecyclerView.Adapter<FBStickerViewHolder> {

    private final int ITEM_TYPE_ONE = 1;

    private final int ITEM_TYPE_TWO = 2;

    private int selectedPosition = FBSelectedPosition.POSITION_MASK;
    private int lastPosition;

    private final List<FBMaskConfig.FBMask> maskList;

    private Handler handler = new Handler();

    private Map<String, String> downloadingMasks = new ConcurrentHashMap<>();

    public FBMaskAdapter(List<FBMaskConfig.FBMask> maskList) {
        this.maskList = maskList;
        DownloadDispatcher.setMaxParallelRunningCount(5);
    }

    @Override
    public int getItemViewType(int position) {
        // if (position == 0) {
        //     return ITEM_TYPE_ONE;
        // } else {
            return ITEM_TYPE_TWO;
        // }
    }

    @NonNull
    @Override
    public FBStickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        // if (viewType == ITEM_TYPE_ONE) {
        //     view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ht_sticker_one, parent, false);
        // } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fb_sticker, parent, false);
        // }
        return new FBStickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FBStickerViewHolder holder, int position) {

        final FBMaskConfig.FBMask fbMask = maskList.get(holder.getAdapterPosition());

        selectedPosition = FBSelectedPosition.POSITION_MASK;
        if (selectedPosition == position) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }

        //显示封面
        if (fbMask == FBMask.NO_MASK) {
            holder.thumbIV.setImageResource(R.mipmap.icon_ht_none_sticker);
        } else {
//            Glide.with(holder.itemView.getContext())
//                .load(maskList.get(position).getIcon())
//                .placeholder(R.drawable.icon_placeholder)
//                .into(holder.thumbIV);
            holder.thumbIV.setImageDrawable(FBMaskResEnum.values()[holder.getAdapterPosition()].getIcon(holder.itemView.getContext()));
        }

        //判断是否已经下载
        if (fbMask.isDownloaded() == FBDownloadState.COMPLETE_DOWNLOAD) {
            holder.downloadIV.setVisibility(View.GONE);
            holder.loadingIV.setVisibility(View.GONE);
            holder.loadingBG.setVisibility(View.GONE);
            holder.stopLoadingAnimation();
        } else {
            //判断是否正在下载，如果正在下载，则显示加载动画
            if (downloadingMasks.containsKey(fbMask.getName())) {
                holder.downloadIV.setVisibility(View.GONE);
                holder.loadingIV.setVisibility(View.VISIBLE);
                holder.loadingBG.setVisibility(View.VISIBLE);
                holder.startLoadingAnimation();
            } else {
                holder.downloadIV.setVisibility(View.VISIBLE);
                holder.loadingIV.setVisibility(View.GONE);
                holder.loadingBG.setVisibility(View.GONE);
                holder.stopLoadingAnimation();
            }
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //如果没有下载，则开始下载到本地
                if (fbMask.isDownloaded() == FBDownloadState.NOT_DOWNLOAD) {
                    int currentPosition = holder.getAdapterPosition();

                    //如果已经在下载了，则不操作
                    if (downloadingMasks.containsKey(fbMask.getName())) {
                        return;
                    }

                    new DownloadTask.Builder(fbMask.getUrl(), new File(FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemMask.getValue())))
                            .setMinIntervalMillisCallbackProcess(30)
                            .setConnectionCount(1)
                            .build()
                            .enqueue(new DownloadListener2() {
                                @Override
                                public void taskStart(@NonNull DownloadTask task) {
                                    downloadingMasks.put(fbMask.getName(), fbMask.getUrl());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyDataSetChanged();
                                        }
                                    });
                                }

                                @Override
                                public void taskEnd(@NonNull final DownloadTask task, @NonNull EndCause cause, @Nullable final Exception realCause) {
                                    downloadingMasks.remove(fbMask.getName());

                                    if (cause == EndCause.COMPLETED) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                File targetDir =
                                                    new File(FBEffect.shareInstance().getARItemPathBy(FBItemEnum.FBItemMask.getValue()));
                                                File file = task.getFile();
                                                try {
                                                    //解压到贴纸目录
                                                    FBUnZip.unzip(file, targetDir);
                                                    if (file != null) {
                                                        file.delete();
                                                    }

                                                    //修改内存与文件
                                                    fbMask.setDownloaded(FBDownloadState.COMPLETE_DOWNLOAD);
                                                    fbMask.downloaded();

                                                    FBEffect.shareInstance().setARItem(FBItemEnum.FBItemMask.getValue(), fbMask.getName());
                                                    lastPosition = selectedPosition;
                                                    selectedPosition = currentPosition;
                                                    FBSelectedPosition.POSITION_MASK = selectedPosition;

                                                    handler.post(new Runnable() {
                                                        @Override
                                                        public void run(){
                                                            notifyDataSetChanged();
                                                        }
                                                    });

                                                } catch (Exception e) {
                                                    if (file != null) {
                                                        file.delete();
                                                    }
                                                }
                                            }
                                        }).start();

                                    } else {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                notifyDataSetChanged();
                                                if (realCause != null) {
                                                    Toast.makeText(holder.itemView.getContext(), realCause.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                    notifyItemChanged(selectedPosition);
                                    notifyItemChanged(lastPosition);
                                }
                            });

                } else {
                    if (holder.getAdapterPosition() == RecyclerView.NO_POSITION) {
                        return;
                    }
                    if (holder.getAdapterPosition() == selectedPosition){
                        //如果点击已选中的效果，则取消效果
                        FBEffect.shareInstance().setARItem(FBItemEnum.FBItemMask.getValue(), "");
                        FBSelectedPosition.POSITION_MASK = -1;
                        notifyItemChanged(selectedPosition);
                        // notifyItemChanged(-1);
                    }else{
                        //如果已经下载了，则让贴纸生效
                        FBEffect.shareInstance().setARItem(FBItemEnum.FBItemMask.getValue(), fbMask.getName());

                        //切换选中背景
                        int lastPosition = selectedPosition;
                        selectedPosition = holder.getAdapterPosition();
                        FBSelectedPosition.POSITION_MASK = selectedPosition;
                        notifyItemChanged(selectedPosition);
                        notifyItemChanged(lastPosition);
                    }



                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return maskList == null ? 0 : maskList.size();
    }
}
