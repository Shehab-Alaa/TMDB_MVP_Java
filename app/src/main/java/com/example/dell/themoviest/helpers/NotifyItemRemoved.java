package com.example.dell.themoviest.helpers;


import java.io.Serializable;

public interface NotifyItemRemoved extends Serializable {
    void onItemRemoved(int moviePosition);
}
