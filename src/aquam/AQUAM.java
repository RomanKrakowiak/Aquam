package aquam;

/**
 *
 * @author rkrakowi
 */
public class AQUAM {

    static GUIAquam interf;

    /**
     * constructor
     */
    private AQUAM() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        interf = new GUIAquam();
        interf.setVisible(true);
    }
}

