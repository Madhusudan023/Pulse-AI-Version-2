
    public class Test {
        public static void main(String[] args) throws Exception {
            Class<?> formsClass = Class.forName("com.google.api.services.forms.v1.Forms");
            for (java.lang.reflect.Method m : formsClass.getMethods()) {
                if (m.getName().equals("forms")) {
                    System.out.println("Forms method return type: " + m.getReturnType().getName());
                    for (java.lang.reflect.Method r : m.getReturnType().getMethods()) {
                        System.out.println("  Forms.FormsOperations method: " + r.getName());
                    }
                }
            }
        }
    }
    
