package com.marginallyclever.makelangeloRobot.generators;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.swing.JPanel;
import com.marginallyclever.makelangelo.Log;
import com.marginallyclever.makelangelo.Translator;
import com.marginallyclever.makelangeloRobot.MakelangeloRobotPanel;


public class Generator_Text extends ImageGenerator {
	// text properties
	private float kerning = 5.0f;
	private float letterWidth = 10.0f;
	private float letterHeight = 20.0f;
	private float lineSpacing = 5.0f;
	private float padding = 5.0f;
	static final String ALPHABET_FOLDER = "ALPHABET/";
	private int charsPerLine = 25;
	private boolean draw_bounding_box = false;

	// text position and alignment
	public enum VAlign {
		TOP, MIDDLE, BOTTOM
	}

	public enum Align {LEFT, CENTER, RIGHT}

	private VAlign align_vertical = VAlign.MIDDLE;
	private Align align_horizontal = Align.CENTER;
	private float posx = 0;
	private float posy = 0;

	private static String lastMessage = "";
	private static int lastFont = 0;
	private static int lastSize = 20;
	private static Font [] fontList;
	private static String [] fontNames;
	
	private MakelangeloRobotPanel robotPanel;

	public Generator_Text() {		
		// build list of fonts
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		fontList = ge.getAllFonts();
		fontNames = new String[fontList.length];
		
		Locale locale = Locale.getDefault();
		int i=0;
		for(Font f : fontList) {
			fontNames[i++] = f.getFontName(locale);
		}
	}
	
	public String [] getFontNames() {
		return fontNames;
	}
	public int getLastFont() {
		return lastFont;
	}
	public void setFont(int arg0) {
		if(arg0<0) arg0=0;
		if(arg0>=fontNames.length) arg0 = fontNames.length-1;
		lastFont = arg0;
	}
	
	
	public int getLastSize() {
		return lastSize;
	}
	public void setSize(int size) {
		if(size<1)size=1;
		lastSize=size;
	}
	
	public String getLastMessage() {
		return lastMessage;
	}
	public void setMessage(String msg) {
		lastMessage = msg;
	}

	@Override
	public String getName() {
		return Translator.get("YourMsgHereName");
	}
	
	@Override
	public JPanel getPanel(MakelangeloRobotPanel arg0) {
		robotPanel = arg0;
		return new Generator_Text_Panel(this);
	}
	
	@Override
	public void regenerate() {
		robotPanel.regenerate(this);
	}

	protected void setupTransform() {
		double imageHeight = machine.getPaperHeight()*machine.getPaperMargin();
		double imageWidth = machine.getPaperWidth()*machine.getPaperMargin();

		double newWidth = imageWidth;
		double newHeight = imageHeight;

		if (imageWidth > machine.getPaperWidth()) {
			float resize = (float) machine.getPaperWidth() / (float) imageWidth;
			newHeight *= resize;
			newWidth = machine.getPaperWidth();
		}
		if (newHeight > machine.getPaperHeight()) {
			float resize = (float) machine.getPaperHeight() / (float) newHeight;
			newWidth *= resize;
			newHeight = machine.getPaperHeight();
		}
		newWidth *= machine.getPaperMargin();
		newHeight *= machine.getPaperMargin();

		textFindCharsPerLine(newWidth);

		posx = 0;
		posy = 0;
	}


	protected void setupTransform(int width, int height) {
		int imageHeight = height;
		int imageWidth = width;

		double newWidth = imageWidth;
		double newHeight = imageHeight;

		if (imageWidth > machine.getPaperWidth()) {
			float resize = (float) machine.getPaperWidth() / (float) imageWidth;
			newHeight *= resize;
			newWidth = machine.getPaperWidth();
		}
		if (newHeight > machine.getPaperHeight()) {
			float resize = (float) machine.getPaperHeight() / (float) newHeight;
			newWidth *= resize;
			newHeight = machine.getPaperHeight();
		}
		newWidth *= machine.getPaperMargin();
		newHeight *= machine.getPaperMargin();

		textFindCharsPerLine(newWidth);

		posx = 0;
		posy = 0;
	}

	
	private void writeBeautifulMessage(String fontName,int fontSize, String message, Writer output) throws IOException {
		if(message.length()<=0) {
			return;
		}
		
		String[] messagePieces=message.split("\n");
		System.out.println("lines of text="+messagePieces.length);
		
		Font font = new Font(fontName, Font.PLAIN, fontSize);
		FontRenderContext frc = new FontRenderContext(null,true,true);

		float yTotal=0;
		float yFirstStep = 0;
		float xMax=0;
		int p;
		for(p=0;p<messagePieces.length;++p) {
			String piece = messagePieces[p];
			if(piece==null || piece.length()==0) piece="\n";
			TextLayout textLayout = new TextLayout(piece,font,frc);
			Shape s = textLayout.getOutline(null);
			Rectangle bounds = s.getBounds();
			yTotal += bounds.getHeight();
			if(yFirstStep==0) yFirstStep = (float)bounds.getHeight();
			if(xMax < bounds.getWidth()) xMax = (float)bounds.getWidth();
		}
		/*
		// display bounding box
		float dx = xMax/2.0f;
		float dy = -(yTotal+yFirstStep/2.0f)/2.0f;

		machine.writeOff(output);
		machine.writeMoveTo(output,-dx, dy);
		machine.writeOn(output);
		machine.writeMoveTo(output, dx, dy);
		machine.writeMoveTo(output, dx,-dy);
		machine.writeMoveTo(output,-dx,-dy);
		machine.writeMoveTo(output,-dx, dy);
		machine.writeOff(output);
		 */
		float dx = xMax / 2.0f;
		float dy = -yTotal/2.0f+yFirstStep/2.0f;

		for(p=0;p<messagePieces.length;++p) {
			String piece = messagePieces[p];
			if(piece==null || piece.length()==0) piece="\n";
			//TextLayout textLayout = new TextLayout(piece,font,frc);
			//Shape s = textLayout.getOutline(null);
			//Rectangle bounds = s.getBounds();

			writeBeautifulString(font,frc,piece,output, dx, dy);
			
			dy += fontSize;//bounds.getHeight();
		}
	}
	
	private void writeBeautifulString(Font font, FontRenderContext frc,String text, Writer output,float dx, float dy) throws IOException { 
		TextLayout textLayout = new TextLayout(text,font,frc);
		Shape s = textLayout.getOutline(null);		
		PathIterator pi = s.getPathIterator(null);
		
		float [] coords = new float[6];
		float [] coords2 = new float[6];
		float [] start = new float[6];
		float n,i;
		n = 5;
		
		while(pi.isDone() == false ) {
			int type = pi.currentSegment(coords);
			switch(type) {
			case PathIterator.SEG_CLOSE:
				//System.out.println("CLOSE");
				machine.writeMoveTo(output, start[0]-dx, -start[1]-dy,false);
				machine.writeOff(output);
				coords2[0] = coords[0];
				coords2[1] = coords[1];
				break;
			case PathIterator.SEG_LINETO:
				//System.out.println("LINE");
				machine.writeMoveTo(output, coords[0]-dx, -coords[1]-dy,false);
				coords2[0] = coords[0];
				coords2[1] = coords[1];
				break;
			case PathIterator.SEG_MOVETO:
				//System.out.println("MOVE");
				// move without drawing
				start[0] = coords2[0] = coords[0];
				start[1] = coords2[1] = coords[1];
				machine.writeMoveTo(output, start[0]-dx, -start[1]-dy,true);
				machine.writeOn(output);
				break;
			case PathIterator.SEG_CUBICTO:
				//P(t) = B(3,0)*CP + B(3,1)*P1 + B(3,2)*P2 + B(3,3)*P3
				//0 <= t <= 1
				//B(n,m) = mth coefficient of nth degree Bernstein polynomial
	            //   = C(n,m) * t^(m) * (1 - t)^(n-m)
				//C(n,m) = Combinations of n things, taken m at a time
	            //   = n! / (m! * (n-m)!)
				
				// B(3,0) = (1 - t)^3
				// B(3,1) = 3 * t * (1 - t)^2
				// B(3,2) = 3 * t^2 * (1 - t)
				// B(3,3) = t^3
				//System.out.println("CUBIC");
				for(i=0;i<n;++i) {
					float t = i/n;
					float t1 = (1.0f-t);
					float a = t1*t1*t1;
					float b = 3*t*t1*t1;
					float c = 3*t*t*t1;
					float d = t*t*t;
					float x = coords2[0]*a + coords[0]*b + coords[2]*c + coords[4]*d;
					float y = coords2[1]*a + coords[1]*b + coords[3]*c + coords[5]*d;
					machine.writeMoveTo(output, x-dx,-y-dy,false);
				}
				machine.writeMoveTo(output, coords[4]-dx,-coords[5]-dy,false);
				coords2[0] = coords[4];
				coords2[1] = coords[5];
				break;
			case PathIterator.SEG_QUADTO:
				//System.out.println("QUAD");
				for(i=0;i<n;++i) {
					float t = i/n;
					//(1-t)²*P0 + 2t*(1-t)*P1 + t²*P2
					float u = (1.0f-t);
					float tt=u*u;
					float ttt=2.0f*t*u;
					float tttt=t*t;
					float x = coords2[0]*tt + (coords[0]*ttt) + (coords[2]*tttt);
					float y = coords2[1]*tt + (coords[1]*ttt) + (coords[3]*tttt);
					machine.writeMoveTo(output, x-dx,-y-dy,false);
				}
				machine.writeMoveTo(output, coords[2]-dx,-coords[3]-dy,false);
				coords2[0] = coords[2];
				coords2[1] = coords[3];
				break;
			}
			pi.next();
		}
	}

	@Override
	public boolean generate(Writer out) throws IOException {
		String fontName = fontNames[lastFont];

		imageStart(out);
		liftPen(out);
		machine.writeChangeTo(out);

		posx=0;
		posy=0;
		textFindCharsPerLine(machine.getPaperWidth()*machine.getPaperMargin());
		textSetAlign(Align.CENTER);
		textSetVAlign(VAlign.MIDDLE);
		writeBeautifulMessage(fontName,lastSize,lastMessage,out);
		liftPen(out);
	    moveTo(out, (float)machine.getHomeX(), (float)machine.getHomeY(),true);
	    return true;
	}
	
	public void textSetPosition(float x, float y) {
		posx = x;
		posy = y;
	}

	public void textSetAlign(Align x) {
		align_horizontal = x;
	}

	public void textSetVAlign(VAlign x) {
		align_vertical = x;
	}


	public void textSetCharsPerLine(int numChars) {
		charsPerLine = numChars;
		//System.out.println("MAX="+numChars);
	}


	public void textFindCharsPerLine(double width) {
		charsPerLine = (int) Math.floor((float) (width * 10.0f - padding * 2.0f) / (float) (letterWidth + kerning));
		//System.out.println("MAX="+chars_per_line);
	}


	/**
	 * calculate the smallest rectangle that would fit around the string of text
	 * @param text the message to fit around
	 * @return a Rectangle2D that describes the minimum fit
	 */
	private Rectangle2D textCalculateBounds(String text) {
		String[] lines = textWrapToLength(text);
		int len = textLongestLine(lines);

		int num_lines = lines.length;
		float h = padding * 2 + (letterHeight + lineSpacing) * num_lines;//- line_spacing; removed because of letters that hang below the line
		float w = padding * 2 + (letterWidth + kerning) * len - kerning;
		float xmax = 0, xmin = 0, ymax = 0, ymin = 0;

		switch (align_horizontal) {
		case LEFT:
			xmax = posx + w;
			xmin = posx;
			break;
		case CENTER:
			xmax = posx + w / 2;
			xmin = posx - w / 2;
			break;
		case RIGHT:
			xmax = posx;
			xmin = posx - w;
			break;
		}

		switch (align_vertical) {
		case BOTTOM:
			ymax = posy + h;
			ymin = posy;
			break;
		case MIDDLE:
			ymax = posy + h / 2;
			ymin = posy - h / 2;
			break;
		case TOP:
			ymax = posy;
			ymin = posy - h;
			break;
		}
		/*
	    System.out.println(num_lines + " lines");
	    System.out.println("longest "+len+" chars");
	    System.out.println("x "+xmin+" to "+xmax);
	    System.out.println("y "+ymin+" to "+ymax);
		 */
		Rectangle2D r = new Rectangle2D.Float();
		r.setRect(xmin, ymin, xmax - xmin, ymax - ymin);

		return r;
	}


	private void textCreateMessageNow(String text, Writer output) throws IOException {
		if (charsPerLine <= 0) return;

		// find size of text block
		Rectangle2D r = textCalculateBounds(text);

		output.write("G90;\n");
		liftPen(output);

		if (draw_bounding_box) {
			// draw bounding box
			output.write("G0 X" + (float) r.getMinX() + " Y" + (float) r.getMaxY() + ";\n");
			lowerPen(output);
			output.write("G0 X" + (float) r.getMaxX() + " Y" + (float) r.getMaxY() + ";\n");
			output.write("G0 X" + (float) r.getMaxX() + " Y" + (float) r.getMinY() + ";\n");
			output.write("G0 X" + (float) r.getMinX() + " Y" + (float) r.getMinY() + ";\n");
			output.write("G0 X" + (float) r.getMinX() + " Y" + (float) r.getMaxY() + ";\n");
			liftPen(output);
		}

		// move to first line height
		// assumes we are still G90
		float message_start = (float) r.getMinX() + padding;
		float firstline = (float) r.getMinY() - (padding + letterHeight);
		float interline = -(letterHeight + lineSpacing);

		output.write("G0 X" + message_start + " Y" + firstline + ";\n");
		output.write("G91;\n");

		// draw line of text
		String[] lines = textWrapToLength(text);
		for (int i = 0; i < lines.length; i++) {
			if (i > 0) {
				// newline
				output.write("G0 Y" + interline + ";\n");

				// carriage return
				output.write("G90;\n");
				output.write("G0 X" + message_start + ";\n");
				output.write("G91;\n");
			}

			textDrawLine(lines[i], output);
		}

		output.write("G90;\n");
		liftPen(output);
	}


	// break the text into an array of strings.  each string is one line of text made to fit into the chars_per_line limit.
	private String[] textWrapToLength(String src) {
		String[] test_lines = src.split("\n");
		int i, j;

		int num_lines = 0;
		for (i = 0; i < test_lines.length; ++i) {
			if (test_lines[i].length() > charsPerLine) {
				int x = (int) Math.ceil((double) test_lines[i].length() / (double) charsPerLine);
				num_lines += x;
			} else {
				num_lines++;
			}
		}

		String[] lines = new String[num_lines];
		j = 0;
		for (i = 0; i < test_lines.length; ++i) {
			if (test_lines[i].length() <= charsPerLine) {
				lines[j++] = test_lines[i];
			} else {
				String[] lineSegments = test_lines[i].split("(?<=\\G.{" + charsPerLine + "})");
				for (String aLine : lineSegments) {
					lines[j++] = aLine;
				}
			}
		}

		return lines;
	}

	private int textLongestLine(String[] lines) {
		int len = 0;
		for (String line : lines) {
			if (len < line.length()) len = line.length();
		}

		return len;
	}

	private void textDrawLine(String a1, Writer output) throws IOException {
		String ud = ALPHABET_FOLDER;

		Log.message( a1 +"("+ a1.length() +")" );

		int i = 0;
		for (i = 0; i < a1.length(); ++i) {
			char letter = a1.charAt(i);

			if (letter == '\n' || letter == '\r') continue;

			String name;

			// find the file that goes with this character
			if ('a' <= letter && letter <= 'z') {
				name = "SMALL_" + Character.toUpperCase(letter);
			} else {
				switch (letter) {
				case ' ':
					name = "SPACE";
					break;
				case '!':
					name = "EXCLAMATION";
					break;
				case '"':
					name = "DOUBLEQ";
					break;
				case '$':
					name = "DOLLAR";
					break;
				case '#':
					name = "POUND";
					break;
				case '%':
					name = "PERCENT";
					break;
				case '&':
					name = "AMPERSAND";
					break;
				case '\'':
					name = "SINGLEQ";
					break;
				case '(':
					name = "B1OPEN";
					break;
				case ')':
					name = "B1CLOSE";
					break;
				case '*':
					name = "ASTERIX";
					break;
				case '+':
					name = "PLUS";
					break;
				case ',':
					name = "COMMA";
					break;
				case '-':
					name = "HYPHEN";
					break;
				case '.':
					name = "PERIOD";
					break;
				case '/':
					name = "FSLASH";
					break;
				case ':':
					name = "COLON";
					break;
				case ';':
					name = "SEMICOLON";
					break;
				case '<':
					name = "GREATERTHAN";
					break;
				case '=':
					name = "EQUAL";
					break;
				case '>':
					name = "LESSTHAN";
					break;
				case '?':
					name = "QUESTION";
					break;
				case '@':
					name = "AT";
					break;
				case '[':
					name = "B2OPEN";
					break;
				case ']':
					name = "B2CLOSE";
					break;
				case '^':
					name = "CARET";
					break;
				case '_':
					name = "UNDERSCORE";
					break;
				case '`':
					name = "GRAVE";
					break;
				case '{':
					name = "B3OPEN";
					break;
				case '|':
					name = "BAR";
					break;
				case '}':
					name = "B3CLOSE";
					break;
				case '~':
					name = "TILDE";
					break;
				case '\\':
					name = "BSLASH";
					break;
				case '…':
					name = "SPACE";
					break;
				default:
					name = Character.toString(letter);
					break;
				}
			}
			String fn = ud + name + ".NGC";
			final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fn);
			if (inputStream != null) {
				if (i > 0 && kerning != 0) {
					output.write("G0 X" + (kerning) + ";\n");
				}
				try (final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
						final BufferedReader in = new BufferedReader(inputStreamReader)) {

					String b;
					while ((b = in.readLine()) != null) {
						if (b.trim().length() == 0)
							continue;
						switch (b) {
						case "UP":
							output.write("G90;\n");
							liftPen(output);
							output.write("G91;\n");
							break;
						case "DOWN":
							output.write("G90;\n");
							lowerPen(output);
							output.write("G91;\n");
							break;
						default:
							StringTokenizer st = new StringTokenizer(b);
							String gap = "";
							while (st.hasMoreTokens()) {
								String c = st.nextToken();
								if (c.startsWith("G")) {
									output.write(gap + c);
								} else if (c.startsWith("X")) {
									// translate coordinates
									final float x = Float.parseFloat(c.substring(1)) * 10; // cm to mm
									output.write(gap + "X" + (x));
								} else if (c.startsWith("Y")) {
									// translate coordinates
									final float y = Float.parseFloat(c.substring(1)) * 10; // cm to mm
									output.write(gap + "Y" + (y));
								} else {
									output.write(gap + c);
								}
								gap = " ";
							}
							output.write(";\n");
							break;
						}
					}
				}
			} else {
				// file not found
				System.out.println("file not found. Making best guess as to where it is.");
				System.out.print(fn);
				System.out.println(" NOK");
			}
		}
	}

	public void signName(Writer out) throws IOException {
		setupTransform();

		textSetAlign(Align.RIGHT);
		textSetVAlign(VAlign.BOTTOM);
		textSetPosition((float)(machine.getPaperWidth() *10.0f*machine.getPaperMargin()),
				(float)(machine.getPaperHeight()*10.0f*machine.getPaperMargin()));

		textSetCharsPerLine(25);

		textCreateMessageNow("Makelangelo #" + Long.toString(machine.getUID()), out);
		//TextCreateMessageNow("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890<>,?/\"':;[]!@#$%^&*()_+-=\\|~`{}.",out);
	}
}
