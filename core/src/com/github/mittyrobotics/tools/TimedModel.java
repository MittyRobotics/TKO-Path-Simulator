package com.github.mittyrobotics.tools;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class TimedModel extends ModelInstance {
    public TimedModel(Model model, Array<TimedModel> list) {
        super(model);

        new Timer(20000, e -> list.removeValue(this, true)).start();
    }
}
