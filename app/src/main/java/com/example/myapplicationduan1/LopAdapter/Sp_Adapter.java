package com.example.myapplicationduan1.LopAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationduan1.LopDao.LoaispDao;
import com.example.myapplicationduan1.LopDao.SanphamDao;
import com.example.myapplicationduan1.LopModel.LoaiSanpham;
import com.example.myapplicationduan1.LopModel.SanPham;
import com.example.myapplicationduan1.R;
import com.example.myapplicationduan1.SpinerAdapter.LoaiSpSpiner;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Sp_Adapter extends RecyclerView.Adapter<Sp_Adapter.SpHoder> implements Filterable {
    Context context;
    List<SanPham> list;
    SanphamDao dao;
    int ms, mst;
    ArrayList<LoaiSanpham> loaiSpches;
    LoaispDao loaispDao;
    List<SanPham> mlistOld;

    public Sp_Adapter(Context context, List<SanPham> list, SanphamDao dao){
        this.context = context;
        this.list = list;
        this.dao = dao;
        this.mlistOld = list;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if (strSearch.isEmpty()) {
                    list = mlistOld;
                } else {
                    List<SanPham> listtv = new ArrayList<>();
                    for (SanPham sanPham : mlistOld) {
                        if (sanPham.getNsx().toLowerCase().contains(strSearch.toLowerCase())) {
                            listtv.add(sanPham);
                        }
                        ;
                    }
                    list = listtv;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (List<SanPham>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public SpHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_sp, parent, false);
        return new SpHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpHoder holder, int position) {
        SanPham sanPham = list.get(position);
        if (sanPham == null) {
            return;
        } else {
            String tenLoai;
            try {
                LoaispDao loaiSpDao = new LoaispDao(context);
                LoaiSanpham loaiSanpham = loaiSpDao.getId(String.valueOf(sanPham.getMalsp()));
                tenLoai = loaiSanpham.getTenLSp();
            } catch (Exception e) {
                tenLoai = "???? x??a lo???i s???n ph???m";
            }

            holder.tv_ms.setText("M?? S???n ph???m: " + sanPham.getMasp() + "");
            holder.tv_mls.setText("Lo???i S???n ph???m: " + tenLoai);
            holder.tv_tens.setText("T??n S???n ph???m: " + sanPham.getTensp());
            holder.tv_nsx.setText("NSX: " + sanPham.getNsx());
            Locale locale = new Locale("nv", "VN");
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
            String tien = numberFormat.format(sanPham.getGiasp());
            holder.tv_gias.setText("Gi?? S???n ph???m: " + tien);
        }
        holder.img_dels.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Delete");
                builder.setIcon(R.drawable.ic_dele);
                builder.setMessage("B???n c?? mu???n x??a kh??ng?");
                builder.setCancelable(true);
                builder.setPositiveButton("C??", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dao = new SanphamDao(context);
                        long kq = dao.DELETES(sanPham);
                        if (kq > 0) {
                            list.clear();
                            list.addAll(dao.GETS());
                            Toast.makeText(context.getApplicationContext(), "X??a Th??nh C??ng", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                            dialog.cancel();
                        } else {
                            Toast.makeText(context.getApplicationContext(), "X??a Th???t B???i", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
        holder.img_edits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.custom_edit_sp, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setView(view);
                builder.setTitle("                S???a S???n ph???m");
                EditText ed_teneds = (EditText) view.findViewById(R.id.tensached);
                Spinner spneds = (Spinner) view.findViewById(R.id.spin_lsached);
                EditText ed_giaeds = (EditText) view.findViewById(R.id.giasached);
                ed_teneds.setText(sanPham.getTensp());
                ed_giaeds.setText(Integer.toString(sanPham.getGiasp()));
                loaiSpches = new ArrayList<>();
                loaispDao = new LoaispDao(view.getContext());
                loaiSpches = (ArrayList<LoaiSanpham>) loaispDao.GETLS();
                LoaiSpSpiner spiner = new LoaiSpSpiner(view.getContext(), loaiSpches);
                spneds.setAdapter(spiner);
                spneds.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ms = loaiSpches.get(position).getMaLSp();
                        Toast.makeText(view.getContext(), "Ch???n: " + loaiSpches.get(position).getTenLSp(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                mst = 0;
                for (int i = 0; i < loaiSpches.size(); i++) {
                    if (sanPham.getMalsp() == loaiSpches.get(i).getMaLSp()) {
                        mst = i;
                    }
                }
                spneds.setSelection(mst);
                builder.setPositiveButton("S???a", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (sanPham.getTensp().equals(ed_teneds.getText().toString()) && sanPham.getGiasp() == Integer.parseInt(ed_giaeds.getText().toString())
                                && sanPham.getMalsp() == mst) {
                            Toast.makeText(context.getApplicationContext(), "Kh??ng C?? G?? Thay ?????i \n   S???a Th???t B???i!", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                int giaThue = Integer.parseInt(ed_giaeds.getText().toString());
                                dao = new SanphamDao(context);
                                sanPham.setTensp(ed_teneds.getText().toString());
                                sanPham.setGiasp(giaThue);
                                sanPham.setMalsp(ms);
                                long kq = dao.UPDATES(sanPham);
                                if (kq > 0) {
                                    list.clear();
                                    list.addAll(dao.GETS());
                                    Toast.makeText(view.getContext(), "S???a Th??nh C??ng", Toast.LENGTH_SHORT).show();
                                    ed_teneds.setText("");
                                    ed_giaeds.setText("");
                                    spneds.setSelection(0);
                                    notifyDataSetChanged();
                                    dialog.cancel();
                                } else {
                                    Toast.makeText(view.getContext(), "S???a Th???t B???i", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(view.getContext(), "Gi?? thu?? ph???i l?? s???", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                builder.setNegativeButton("H???y", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class SpHoder extends RecyclerView.ViewHolder{
        TextView tv_ms, tv_mls, tv_tens, tv_gias, tv_nsx;
        ImageView img_dels, img_edits;
        ConstraintLayout cns_lays;
        public SpHoder(@NonNull View itemView) {
            super(itemView);
            tv_ms = itemView.findViewById(R.id.tv_masach);
            tv_mls = itemView.findViewById(R.id.tv_maloais);
            tv_tens = itemView.findViewById(R.id.tv_tensach);
            tv_gias = itemView.findViewById(R.id.tv_giasach);
            tv_nsx = itemView.findViewById(R.id.tv_tacgia);
            img_dels = itemView.findViewById(R.id.img_deltsach);
            img_edits = itemView.findViewById(R.id.img_editsach);
            cns_lays = itemView.findViewById(R.id.conss);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.transition);
            cns_lays.setAnimation(animation);
        }
    }
}
