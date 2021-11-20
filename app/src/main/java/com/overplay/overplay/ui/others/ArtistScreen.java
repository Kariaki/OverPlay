package com.overplay.overplay.ui.others;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.overplay.overplay.R;
import com.overplay.overplay.adapter.ViewHolders.Plugs.SongsPlug;
import com.overplay.overplay.database.OverPlayViewModel;
import com.overplay.overplay.database.entities.MusicItem;
import com.overplay.overplay.databinding.ArtistScreenBinding;
import com.votenoid.votenoid.Adapter.GeneralAdapter;
import com.votenoid.votenoid.Adapter.SuperClickListener;

import java.util.ArrayList;
import java.util.List;

public class ArtistScreen extends Fragment {


    private ArtistScreenBinding binding;

    private GeneralAdapter generalAdapter;
    private List<MusicItem> musicItems = new ArrayList<>();
    private OverPlayViewModel viewModel;
    private ArtistScreenArgs args;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = ArtistScreenBinding.inflate(inflater);
        args=ArtistScreenArgs.fromBundle(getArguments());
        viewModel = new ViewModelProvider(requireActivity()).get(OverPlayViewModel.class);

        generalAdapter = new GeneralAdapter();
        generalAdapter.setItems(musicItems);
        generalAdapter.setSuperClickListener(clickListener);
        generalAdapter.setViewHolderPlug(SongsPlug.INSTANCE.getPlug());

        populateUser(args.getArtist());

        binding.musicList.hasFixedSize();
        binding.musicList.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.musicList.setAdapter(generalAdapter);

        return binding.getRoot();
    }

    private void populateUser(String artist) {
        viewModel.artistSongs(artist).observe(getViewLifecycleOwner(), new Observer<List<MusicItem>>() {
            @Override
            public void onChanged(List<MusicItem> items) {
                musicItems=items;
                generalAdapter.notifyDataSetChanged();
            }
        });
    }

    SuperClickListener clickListener = new SuperClickListener() {
        @Override
        public void onClickItem(int position) {
            MusicItem item = musicItems.get(position);
        }

        @Override
        public void onClickIcon(int position) {

        }
    };

}