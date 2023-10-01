package com.example.myshots;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public
class MyAdapter extends FirebaseRecyclerAdapter<model, MyAdapter.myviewholder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public
    MyAdapter(@NonNull FirebaseRecyclerOptions<model> options) {
        super(options);
    }

    @Override
    protected
    void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull model model) {

//        String mill = String.valueOf(model.getTimestamp());

        holder.nameText.setText(model.getName());
        holder.noteText.setText(model.getNote());
//        holder.noteTime.setText(mill);

        Glide.with(holder.img1.getContext()).load(model.getPimage()).placeholder(R.drawable.ic_image).into(holder.img1);

        holder.img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public
            void onClick(View view) {
                Toasty.info(view.getContext(), "Event details", Toasty.LENGTH_SHORT, true).show();
                Intent intent = new Intent(view.getContext(), DetailActivity.class);
                intent.putExtra("nam", model.getName());
                intent.putExtra("not", model.getNote());
                intent.putExtra("image", model.getPimage());
//                intent.putExtra("timing", model.getTimestamp());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);

//                Toast.makeText(view.getContext(), "Event details", Toast.LENGTH_SHORT).show();
            }
        });
        holder.img1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public
            boolean onLongClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Selected");
                builder.setMessage("Choose your action for this event");
                builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public
                    void onClick(DialogInterface dialogInterface, int i) {
//                        BitmapDrawable drawable = (BitmapDrawable) holder.img1.getDrawable();
//                        Bitmap bitmap = drawable.getBitmap();
//                        String bitmapPath = MediaStore.Images.Media.insertImage(view.getContext().getContentResolver(), bitmap, "title", null);
//
//                        Uri uri = Uri.parse(bitmapPath);
//
//                        Intent intent  = new Intent(Intent.ACTION_SEND);
//                        intent.setType("image/*");
//                        intent.putExtra(Intent.EXTRA_STREAM, uri);
//                        intent.putExtra(Intent.EXTRA_TEXT, "My event...");
//                        view.getContext().startActivity(Intent.createChooser(intent, "Share through"));
                        Toasty.info(view.getContext(), "Jaan\uD83E\uDD17\uD83D\uDE0B...it's coming soon...", Toasty.LENGTH_SHORT, true).show();
//                        Toast.makeText(view.getContext(), "Jaan🤗😋...it's coming soon...", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public
                    void onClick(DialogInterface dialogInterface, int i) {
                        final DialogPlus dialogPlus = DialogPlus.newDialog(view.getContext())
                                .setContentHolder(new ViewHolder(R.layout.dialog_content))
                                .setExpanded(true, 620)
                                .setGravity(Gravity.TOP)
                                .create();
                        View myview = dialogPlus.getHolderView();
                        EditText name = myview.findViewById(R.id.etUpdName);
                        EditText note = myview.findViewById(R.id.etUpdNote);
//                        EditText pimage = myview.findViewById(R.id.etUpdImageUrl);
                        Button btnUpdate = myview.findViewById(R.id.btnUpdate);

//                        pimage.setText(model.getPimage());
                        name.setText(model.getName());
                        note.setText(model.getNote());

                        dialogPlus.show();

                        btnUpdate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public
                            void onClick(View view) {
                                Map<String, Object> map = new HashMap<>();

//                                map.put("pimage", pimage.getText().toString());
                                map.put("name", name.getText().toString());
                                map.put("note", note.getText().toString());

                                FirebaseDatabase.getInstance().getReference().child("events")
                                        .child(getRef(position).getKey())
                                        .updateChildren(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public
                                            void onSuccess(Void unused) {
                                                dialogPlus.dismiss();
                                                Toasty.success(view.getContext(), "Event updated successfully!", Toasty.LENGTH_SHORT, true).show();
//                                                Toast.makeText(view.getContext(), "Event Updated successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public
                                            void onFailure(@NonNull Exception e) {
                                                dialogPlus.dismiss();
                                                Toasty.error(view.getContext(), "Failed to update event!", Toasty.LENGTH_SHORT, true).show();
//                                                Toast.makeText(view.getContext(), "Failed to update event !", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                }).setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public
                    void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                        builder1.setTitle("Delete");
                        builder1.setMessage("Are you sure ?");
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public
                            void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase.getInstance().getReference().child("events").child(getRef(position).getKey()).removeValue();
                                Toasty.success(view.getContext(), "Event deleted successfully!", Toasty.LENGTH_SHORT, true).show();
//                                Toast.makeText(view.getContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public
                            void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

                    }
                }).show();
                return true;
            }
        });

    }

    @NonNull
    @Override
    public
    myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show, parent, false);
        return new myviewholder(view);
    }

    public class myviewholder extends RecyclerView.ViewHolder{

        ImageView img1;
        TextView nameText, noteText, noteTime;

        public
        myviewholder(@NonNull View itemView) {
            super(itemView);

            img1 = itemView.findViewById(R.id.img1);
            nameText = itemView.findViewById(R.id.nameText);
            noteText = itemView.findViewById(R.id.noteText);
            noteTime = itemView.findViewById(R.id.noteTime);
        }
    }
}
