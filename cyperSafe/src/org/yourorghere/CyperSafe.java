/* CPCS391_Project 
Group: 4*/

// Package declaration
package org.yourorghere;

// Import statements
import com.sun.opengl.util.Animator;
import com.sun.opengl.util.GLUT;
import java.awt.Frame;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import java.awt.event.*;

/**
 * CyperSafe.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel)
 * <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */
public class CyperSafe implements GLEventListener, MouseListener, MouseMotionListener {
    
    // Variables for mouse interaction
    private int prevMouseX, prevMouseY;
    private boolean mouseRButtonDown = false;
    private float rotationX = 0;
    private float rotationY = 0;

    // Main method
    public static void main(String[] args) {
        // Create frame and canvas
        Frame frame = new Frame("Simple JOGL Application");
        GLCanvas canvas = new GLCanvas();

        // Create instance of CyperSafe
        CyperSafe demo = new CyperSafe();

        // Add listeners to canvas
        canvas.addGLEventListener(demo);
        canvas.addMouseListener(demo);
        canvas.addMouseMotionListener(demo);

        // Add canvas to frame
        frame.add(canvas);
        frame.setSize(640, 480);

        // Create animator for canvas
        final Animator animator = new Animator(canvas);

        // Add window listener to handle closing event
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
                new Thread(new Runnable() {
                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });

        // Center frame and make it visible
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        animator.start();
    }

    // Initialize method
    public void init(GLAutoDrawable drawable) {
        // Get GL instance
        GL gl = drawable.getGL();
        System.err.println("INIT GL IS: " + gl.getClass().getName());

        // Enable VSync
        gl.setSwapInterval(1);

        // Set material properties and lighting
        float[] ambiColor = {0f, 0f, 1f, 0f};
        float[] deffColor = {0.8f, 0.8f, 0.8f, 1.0f};
        float[] specColor = {22.0f, 22.0f, 0.0f, 0.0f};
        float[] lightPosition = {2.0f, 2.0f, 2.0f, 1.0f};
        float[] shininess = {10.0f}; //the smaller value, the higher shine

        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, ambiColor, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, deffColor, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, specColor, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, shininess, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPosition, 0);

        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);

        // Set background color and shading mode
        gl.glClearColor(100 / 255f, 227 / 255f, 234 / 255f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.
        gl.glEnable(GL.GL_TEXTURE_2D); //activate texture mapping for 2D 
    }

    // Reshape method
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();

        // Prevent divide by zero error
        if (height <= 0) {
            height = 1;
        }

        // Calculate aspect ratio
        final float h = (float) width / (float) height;

        // Set up perspective projection
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 1.0, 20.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    private float wingRotationAngle = 0.0f;
    private boolean wingDirection = true; // true for forward, false for backward

    // Display method
    public void display(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        GLUT glut = new GLUT();

        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // Reset the current matrix to the "identity"
        gl.glLoadIdentity();

        gl.glColor3f(0.6f, 0.6f, 0.6f);

        // Move the "drawing cursor" around
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
        gl.glRotatef(rotationX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(rotationY, 0.0f, 1.0f, 0.0f);

        // Draw body parts
        glut.glutSolidSphere(0.75, 30, 16);
        gl.glTranslatef(0f, -0.3f, 0f);
        glut.glutSolidSphere(0.6, 30, 2);
        gl.glTranslatef(0f, 0.3f, 0f);
        gl.glColor3f(0f, 0f, 0f);
        glut.glutWireTorus(0.2, 0.3, 60, 30);
        gl.glColor3f(0f, 0f, 1f);
        glut.glutWireTorus(0.15, 0.2, 60, 30);
        gl.glColor3f(0f, 0f, 0f);
        glut.glutWireTorus(0.1, 0.1, 60, 30);
        gl.glColor3f(0f, 0f, 1f);
        glut.glutWireTorus(0.05, 0.05, 60, 30);
        gl.glColor3f(1f, 1f, 1f);
        glut.glutWireTorus(0.01, 0.03, 60, 30);

        // Draw 3D wings
        gl.glColor3f(0.6f, 0.6f, 0.6f);

        // Right wing with rotation animation
        gl.glPushMatrix();
        gl.glTranslatef(1.0f, 0.0f, 0.0f); // Move to the right side
        gl.glRotatef(-wingRotationAngle, -wingRotationAngle, 1.0f, 0.0f); // Rotate the wing
        gl.glRotatef(-10000000000000000000000000000000f, -1000000000.0f, 1.0f, 0.0f); // Rotate the wing
        glut.glutSolidCone(0.4, 1.0, 10, 10); // Main wing body
        gl.glPopMatrix();

        // Left wing with rotation animation
        gl.glPushMatrix();
        gl.glTranslatef(-1.0f, 0.0f, 0.0f); // Move to the left side
        gl.glRotatef(-wingRotationAngle, -wingRotationAngle, 1.0f, 0.0f); // Rotate the wing
        gl.glRotatef(-10000000000000000000000000000000f, -1000000000.0f, 1.0f, 0.0f); // Rotate the wing
        glut.glutSolidCone(0.4, 1.0, 10, 10); // Main wing body
        gl.glPopMatrix();

        // Update wing rotation angle
        if (wingDirection) {
            wingRotationAngle += 1.0f; // Increase the rotation angle
            if (wingRotationAngle >= 45.0f) {
                wingDirection = false; // Change direction when reaching the limit
            }
        } else {
            wingRotationAngle -= 1.0f; // Decrease the rotation angle
            if (wingRotationAngle <= -45.0f) {
                wingDirection = true; // Change direction when reaching the limit
            }
        }

        // Flush all drawing operations to the graphics card
        gl.glFlush();
    }

    // Method to handle display change events
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
        // No implementation needed
    }

    // Method to handle mouse enter events
    public void mouseEntered(MouseEvent me) {
        // No implementation needed
    }

    // Method to handle mouse exit events
    public void mouseExited(MouseEvent me) {
        // No implementation needed
    }

    // Method to handle mouse click events
    public void mouseClicked(MouseEvent me) {
        // No implementation needed
    }

    // Method to handle mouse move events
    public void mouseMoved(MouseEvent me) {
        // No implementation needed
    }

    // Method to handle mouse press events
    public void mousePressed(MouseEvent me) {
        // Store initial mouse position and set mouse button state
        prevMouseX = me.getX();
        prevMouseY = me.getY();
        mouseRButtonDown = true;
    }

    // Method to handle mouse release events
    public void mouseReleased(MouseEvent me) {
        // Reset mouse button state
        mouseRButtonDown = false;
    }

    // Method to handle mouse drag events
    public void mouseDragged(MouseEvent me) {
        // If right mouse button is down, update rotation based on mouse movement
        if (mouseRButtonDown) {
            int currentX = me.getX();
            int currentY = me.getY();
            rotationY += (currentX - prevMouseX) * 0.5f;
            rotationX += (currentY - prevMouseY) * 0.5f;
            prevMouseX = currentX;
            prevMouseY = currentY;
        }
    }
}
