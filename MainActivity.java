package com.example.myapplication;


import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {
    private ArFragment arFragment;
    private String ASSET_3D ="https://github.com/Aks01singh/KICH/blob/main/parallel1.glb?raw=true";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arFragment=(ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        arFragment.setOnTapArPlaneListener((hitResult, plane,motionEvent) ->{
            placeModel(hitResult.createAnchor());
        });
    }

    private void placeModel(Anchor anchor) {
        ModelRenderable
                .builder()
                .setSource(
                        this,
                        RenderableSource
                        .builder()
                        .setSource(this, Uri.parse(ASSET_3D) ,RenderableSource.SourceType.GLB)
                        .setScale(.75f)
                        .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                        .build()
                )
                .setRegistryId(ASSET_3D)
                .build()
                .thenAccept(modelRenderable -> addNodeToScene(modelRenderable,anchor))
                .exceptionally(throwable ->{
                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    builder.setMessage(throwable.getMessage()).show();
                    return null;
                });

    }

    private void addNodeToScene(ModelRenderable modelRenderable, Anchor anchor) {
        AnchorNode anchorNode=new AnchorNode(anchor);

        anchorNode.setParent(arFragment.getArSceneView().getScene()); //try not to change
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem()); //added later

        transformableNode.setParent(anchorNode); //dont change
        transformableNode.setRenderable(modelRenderable); //only non rotational is displayed without this
        transformableNode.select(); //without this only non rotational one is displayed
        //anchorNode.setRenderable(modelRenderable);
        //arFragment.getArSceneView().getScene().addChild(anchorNode);// might be changed but try not to


    }
    public class CustomArFrag extends ArFragment {
        @Override
        protected Config getSessionConfiguration(Session session) {
            Config config = super.getSessionConfiguration(session);
            config.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL);
            return config;
        }
    }
}
