/**
 * Created by Администратор on 20.05.2017.
 */



import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;


@SuppressWarnings("serial")
public class Paint extends JFrame {

    boolean flagUpdate = true;
    JButton brushBut, lineBut, ellipseBut, rectBut, strokeBut, clearBut;
    ArrayList<Shape> shapes = new ArrayList<Shape>();
    ArrayList<Color> shapeStroke = new ArrayList<Color>();

    static ObjectInputStream in;
    static ObjectOutputStream out;

    Graphics2D graphSettings;

    int currentAction = 1;

    Color strokeColor = Color.BLACK;


    public static void main(String[] args) throws IOException, ClassNotFoundException {

        API.createSocket();
        new Paint();

    }

    public Paint() {

        this.setSize(800, 600);
        this.setTitle("Java Paint");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel buttonPanel = new JPanel();

        Box theBox = Box.createHorizontalBox();


        brushBut = makeMeButtons("Brush", 1);
        lineBut = makeMeButtons("Line", 2);
        ellipseBut = makeMeButtons("Ellipse", 3);
        rectBut = makeMeButtons("Rectangle", 4);


        strokeBut = makeMeColorButton("Stroke", 5, true);
        clearBut = makeMeColorButton("Clear", 6, false);

        theBox.add(brushBut);
        theBox.add(lineBut);
        theBox.add(ellipseBut);
        theBox.add(rectBut);
        theBox.add(strokeBut);
        theBox.add(clearBut);
        buttonPanel.add(theBox);
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.add(new DrawingBoard(), BorderLayout.CENTER);
        this.setVisible(true);
    }


    public JButton makeMeButtons(String name, final int actionNum) {
        JButton theBut = new JButton();
        theBut.setText(name);
        theBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentAction = actionNum;
            }
        });
        return theBut;
    }

    public JButton makeMeColorButton(String name, final int actionNum, final boolean stroke) {
        JButton theBut = new JButton();
        theBut.setText(name);
        theBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (stroke) {
                    strokeColor = JColorChooser.showDialog(null, "Pick a Fill", Color.BLACK);
                } else {
                    API.sendClear();
                }
            }
        });
        return theBut;
    }


    private class DrawingBoard extends JComponent {

        Point drawStart, drawEnd;
        public DrawingBoard() {
            this.addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent e) {
                    if (currentAction != 1) {
                        drawStart = new Point(e.getX(), e.getY());
                        drawEnd = drawStart;
                        repaint();
                    }
                }


                public void mouseReleased(MouseEvent e) {


                    if (currentAction != 1) {

                        Shape aShape = null;
                        if (currentAction == 2) {
                            aShape = drawLine(drawStart.x, drawStart.y,
                                    e.getX(), e.getY());
                            API.sendLine(drawStart.x, drawStart.y, e.getX(), e.getY(), strokeColor);
                        } else if (currentAction == 3) {
                            aShape = drawEllipse(drawStart.x, drawStart.y,
                                    e.getX(), e.getY());
                            API.sendCircle(drawStart.x, drawStart.y, e.getX(), e.getY(), strokeColor);
                        } else if (currentAction == 4) {
                            aShape = drawRectangle(drawStart.x, drawStart.y,
                                    e.getX(), e.getY());
                            API.sendRect(drawStart.x, drawStart.y, e.getX(), e.getY(), strokeColor);
                        }

                        shapes.add(aShape);
                        shapeStroke.add(strokeColor);
                        drawStart = null;
                        drawEnd = null;

                        repaint();
                        API.shapes.clear();
                        API.shapeStroke.clear();
                    }
                }
            });
            this.addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {

                    if (currentAction == 1) {
                        int x = e.getX();
                        int y = e.getY();
                        Shape aShape = null;
                        API.sendPoint(x, y, strokeColor);
                        aShape = drawBrush(x, y, 5, 5);
                        shapes.add(aShape);
                        shapeStroke.add(strokeColor);
                    }

                    drawEnd = new Point(e.getX(), e.getY());
                    repaint();
                }
            });
        }

        public void update() {
            if (API.delFalg) {
                shapes.clear();
                shapeStroke.clear();
                flagUpdate = true;
                API.delFalg = false;
            } else if (flagUpdate) {
                shapes.addAll(API.shapes);
                shapeStroke.addAll(API.shapeStroke);

                flagUpdate = false;
            } else  {

                shapes.add(API.shapes.get(API.shapes.size() - 1));
                shapeStroke.add(API.shapeStroke.get(API.shapeStroke.size() - 1));
            }
            repaint();
        }


        public void paint(Graphics g) {
            if (!API.shapes.isEmpty() && !API.shapeStroke.isEmpty()) {
                update();

                graphSettings = (Graphics2D) g;

                graphSettings.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                graphSettings.setStroke(new BasicStroke(4));

                Iterator<Color> strokeCounter = shapeStroke.iterator();

                for (Shape s : shapes) {

                    graphSettings.setPaint(strokeCounter.next());
                    graphSettings.draw(s);
                }

                if (drawStart != null && drawEnd != null) {

                    graphSettings.setComposite(AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER, 0.40f));

                    graphSettings.setPaint(Color.LIGHT_GRAY);
                    Shape aShape = null;
                    if (currentAction == 2) {
                        aShape = drawLine(drawStart.x, drawStart.y,
                                drawEnd.x, drawEnd.y);
                    } else if (currentAction == 3) {
                        aShape = drawEllipse(drawStart.x, drawStart.y,
                                drawEnd.x, drawEnd.y);
                    } else if (currentAction == 4) {
                        aShape = drawRectangle(drawStart.x, drawStart.y,
                                drawEnd.x, drawEnd.y);
                    }
                    graphSettings.draw(aShape);
                }
            }
        }

        private Rectangle2D.Float drawRectangle(
                int x1, int y1, int x2, int y2) {


            int x = Math.min(x1, x2);
            int y = Math.min(y1, y2);

            int width = Math.abs(x1 - x2);
            int height = Math.abs(y1 - y2);
            return new Rectangle2D.Float(
                    x, y, width, height);
        }

        private Ellipse2D.Float drawEllipse(
                int x1, int y1, int x2, int y2) {
            int x = Math.min(x1, x2);
            int y = Math.min(y1, y2);
            int width = Math.abs(x1 - x2);
            int height = Math.abs(y1 - y2);
            return new Ellipse2D.Float(
                    x, y, width, height);
        }

        private Line2D.Float drawLine(
                int x1, int y1, int x2, int y2) {
            return new Line2D.Float(
                    x1, y1, x2, y2);
        }

        private Ellipse2D.Float drawBrush(
                int x1, int y1, int brushStrokeWidth, int brushStrokeHeight) {
            return new Ellipse2D.Float(
                    x1, y1, brushStrokeWidth, brushStrokeHeight);
        }
    }
}