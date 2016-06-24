package baram.view.cubeplot;

import java.awt.BorderLayout;

public class CubePlot extends javax.swing.JFrame {
	
	private static final long serialVersionUID = -201508211013L;
	
	private javax.swing.JPanel centerPanel;
	
	public CubePlot () {
		
		setTitle("Cube Plot");
        initComponents();
        setSize(1024, 768);
        
        SurfaceModel model = new SurfaceModel();
        model.setXAxisLabel("Data Diversity");
        model.setYAxisLabel("Data Density");
        model.setZAxisLabel("Cost or Cost Ratio");
        
        SurfaceCanvas canvas = new SurfaceCanvas();
        canvas.setModel(model);
        centerPanel.add(canvas, BorderLayout.CENTER);
        canvas.repaint();
        
        setVisible(true);
	}
	
	private void initComponents() {
        centerPanel = new javax.swing.JPanel();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        centerPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);
        pack();
    }
	
	public static void main(String args[]) throws Exception {
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CubePlot().setVisible(true);
            }
        });
	}
}
