/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package locationassignmentnew1;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//package locationassignmentnew;
import java.util.Date;
import java.io.*;
import java.util.*;
import java.lang.*;
import javax.swing.*;
import java.text.SimpleDateFormat;

class dialogbox {
    void dialogbox(String title, String content) {
        JFrame frame = new JFrame();
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
        frame.setLocationRelativeTo(null);
        JOptionPane.showMessageDialog(frame, content, title,
                                      JOptionPane.ERROR_MESSAGE);
        frame.dispose();
    }
}

class myproperties {
    String getproperties(String properties) {
        //\u8BFB\u6587\u4EF6\u6240\u5728\u7684\u6839\u76EE\u5F55
        Properties properties1 = System.getProperties();
        String path = properties1.getProperty("user.dir");
        String rootpath = path.substring(0, 1);
        //\u914D\u7F6E\u6587\u4EF6\u5305\u62EC\u76EE\u5F55\u548C\u5C5E\u6027
        InputStream in;
        Properties p = new Properties();
        try {
            try {
                in = new BufferedInputStream(new FileInputStream(
                        rootpath +
                        "/Users/jacqueline/Code/location assignment/data new1/config.properties"));
                p.load(in);
                String pro_path = p.getProperty("path");
                p.setProperty("path", rootpath + pro_path);
            } catch (FileNotFoundException e) {
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("\u8B66\u544A",
                                 "\u53EF\u80FD\u7684\u539F\u56E0\u662F\uFF1A1.config.properties\u6587\u4EF6\u4E0D\u5B58\u5728\uFF1B2." +
                                 e.toString());
            }
        } catch (IOException e) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("\u8B66\u544A ",
                             "\u53EF\u80FD\u7684\u539F\u56E0\u662F\uFF1A1.config.properties\u6587\u4EF6\u4E0D\u5B58\u5728\uFF1B2." +
                             e.toString());
        }
        return p.getProperty(properties);
    }
}

class Dynamic_programming {
    int state_size;
    double final_value;
    String[] next_state_number;
    int[][] next_state_weight;
    int[] next_weight_group;
    double[] next_value;
    int[] tier_no;
    int[] state_number_chosen;
    int[] state_weight_chosen;

    int calculate_rehandle(int sc, int gn){
        int rehandle=0;
        if(gn==1)
            rehandle=sc/10000+(sc%10000)/1000+(sc%1000)/100+(sc%100)/10;
        if(gn==10)
            rehandle=sc/10000+(sc%10000)/1000+(sc%1000)/100;
        if(gn==100)
            rehandle=sc/10000+(sc%10000)/1000;
        if(gn==1000)
            rehandle=sc/10000;
        if(gn==10000)
            rehandle=0;
        return rehandle;
    }

   double calculate_value(int t, String nc, int[] sc, int[] gn, String[] nc_pre, int[][] sc_pre, double[] va_pre, double[] ratio){
       next_state_number = new String[gn.length];
       next_state_weight = new int[gn.length][sc.length];
       next_weight_group = new int[gn.length];
       next_value = new double[gn.length];
       tier_no = new int[gn.length];
       state_number_chosen = new int[gn.length];
       state_weight_chosen = new int[gn.length];

        double[] objective_value = new double[gn.length];
        for(int i=0; i<gn.length; i++)
            objective_value[i] = Double.MAX_VALUE;
        for(int i=0; i<gn.length; i++){ //gn.length kinds of arrival
            int rehandle_times_temp = Integer.MAX_VALUE;
            for(int j=0; j<nc.length(); j++){ //there are at most nc.length possible locations
                if(nc.charAt(j)>'0'){
                    String nc_temp = nc;
                    int[] sc_temp = new int[sc.length];
                    for(int k=0; k<sc.length; k++)
                        sc_temp[k] = sc[k];
                    int tier_no_temp = Integer.parseInt(String.valueOf(nc.charAt(j)));
                    int state_weight_chosen_temp = sc[j];
                    int rehandle_times =calculate_rehandle(sc_temp[j], gn[i]);
                    if(objective_value[i] > rehandle_times){
                        //adjust nc
                        String first_part = "";
                        String third_part = "";
                        String second_part = "";
                        if(j>0)
                            first_part = nc_temp.substring(0, j-1-0+1);
                        if(j<nc.length()-1)
                            third_part = nc_temp.substring(j+1, nc_temp.length()-1-(j+1)+1 + (j+1));
                        int left_empty = Integer.parseInt(String.valueOf(nc_temp.charAt(j)))-1;
                        second_part = String.valueOf(left_empty);
                        nc_temp = first_part + second_part + third_part;
                        //adjust sc
                        if(left_empty == 0){
                            sc_temp[j] = t;
                        }
                        else{
                            sc_temp[j] = sc_temp[j]+gn[i];
                        }

                        //re-sequence nc and sc
                        for(int k=0; k<nc_temp.length()-1; k++){
                            int first = Integer.parseInt(String.valueOf(nc_temp.charAt(k)));
                            int second = Integer.parseInt(String.valueOf(nc_temp.charAt(k+1)));
                            String first_part2 = "";
                            String fourth_part2 ="";
                            String second_part2 ="";
                            String third_part2 = "";
                            if(first < second){
                                if(k>0)
                                    first_part2 = nc_temp.substring(0, k-1-0+1);
                                if(k<nc.length()-2)
                                    fourth_part2 = nc_temp.substring(k+2, nc_temp.length()-1-(k+2)+1+(k+2));
                                second_part2 = String.valueOf(second);
                                third_part2 = String.valueOf(first);
                                nc_temp = first_part2 + second_part2 + third_part2 + fourth_part2;
                                //re-sequence sc
                                int temp_sc = sc_temp[k+1];
                                sc_temp[k+1]=sc_temp[k];
                                sc_temp[k]=temp_sc;
                            }
                        }
                        //re-sequence sc
                        boolean needed=true;
                        while(needed){
                            needed=false;
                            for(int k=0; k<nc_temp.length()-1; k++){
                                int first = Integer.parseInt(String.valueOf(nc_temp.charAt(k)));
                                int second = Integer.parseInt(String.valueOf(nc_temp.charAt(k+1)));
                                int first1=sc_temp[k];
                                int second1=sc_temp[k+1];
                                if(first == second && first1<second1){
                                    needed=true;
                                    int temp_sc = sc_temp[k+1];
                                    sc_temp[k+1]=sc_temp[k];
                                    sc_temp[k]=temp_sc;
                                }
                            }
                        }
                        
                        int start_index=0;
                        for(int k=0; k<nc_pre.length; k++){
                            if(nc_temp.equals(nc_pre[k])){
                                start_index=k;
                                break;
                            }
                        }

                        int start=start_index;
                        int interval=100;
                        while(start<nc_pre.length){
                            if(nc_temp.equals(nc_pre[start])){
                                boolean small=false;                               
                                for(int m=0; m<sc_temp.length; m++){
                                    if(sc_temp[m] < sc_pre[start][m]){
                                        boolean allequal=true;
                                        for(int k=0; k<m; k++){
                                            if(sc_temp[k] != sc_pre[start][k]){
                                                allequal=false;
                                                break;
                                            }
                                        }
                                        if(allequal){
                                            small=true;
                                            break;
                                        }
                                    }
                                }
                                
                                if(small){
                                   int next_start=start+interval;
                                   if(next_start<nc_pre.length){
                                        if(nc_temp.compareTo(nc_pre[next_start])>0){
                                            break;
                                        }
                                        if(nc_temp.equals(nc_pre[next_start])){
                                            boolean small2=false;
                                            for(int m=0; m<sc_temp.length; m++){
                                                if(sc_temp[m] < sc_pre[next_start][m]){
                                                    boolean allequal2=true;
                                                    for(int k=0; k<m; k++){
                                                        if(sc_temp[k] != sc_pre[next_start][k]){
                                                            allequal2=false;
                                                            break;
                                                        }
                                                    }
                                                    if(allequal2){
                                                        small2=true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if(small2){
                                                start=start+interval+1;
                                            }
                                            if(!small2){
                                                break;
                                            }
                                        }
                                    }
                                   else{
                                       break;
                                   }
                                }
                                else{
                                    break;
                                }
                            }
                        }
                        //boolean find = false;
                        double following_value = 0.0;
                        for(int k=start; k<nc_pre.length; k++){
                            if(nc_temp.equals(nc_pre[k])){
                                boolean match = true;
                                for(int m=0; m<sc_temp.length; m++)
                                    if(sc_temp[m]!= sc_pre[k][m]){
                                        match = false;
                                        break;
                                }
                                if(match){
                                    //find=true;
                                    following_value = va_pre[k];
                                    break;
                                }
                            }
                        }
                        //if(find)
                            //System.out.println("findfindfindfind");
                        //else
                            //System.out.println("nofindnofindnofindnofind");
                        //match the states;
                        /*
                        double following_value = 0.0;
                        for(int k=0; k<nc_pre.length; k++){
                            if(nc_temp.equals(nc_pre[k])){
                                boolean match = true;
                                for(int m=0; m<sc_temp.length; m++)
                                    if(sc_temp[m]!= sc_pre[k][m]){
                                        match = false;
                                        break;
                                }
                                if(match){
                                    following_value = va_pre[k];
                                    break;
                                }
                            }
                        }
*/
                        //calculate objective value
                        double value_temp = following_value + rehandle_times;
                        if(objective_value[i] > value_temp){
                            rehandle_times_temp = rehandle_times;
                            objective_value[i]=value_temp;
                            next_state_number[i] = nc_temp;
                            for(int m=0; m<sc_temp.length; m++)
                                next_state_weight[i][m] = sc_temp[m];
                            next_weight_group[i] = gn[i];
                            next_value[i] = rehandle_times;
                            tier_no[i] = tier_no_temp;
                            state_number_chosen[i] = tier_no_temp;
                            state_weight_chosen[i] = state_weight_chosen_temp;
                        }
                        if(objective_value[i] == value_temp && rehandle_times < rehandle_times_temp){
                            rehandle_times_temp = rehandle_times;
                            objective_value[i]=value_temp;
                            next_state_number[i] = nc_temp;
                            for(int m=0; m<sc_temp.length; m++)
                                next_state_weight[i][m] = sc_temp[m];
                            next_weight_group[i] = gn[i];
                            next_value[i] = rehandle_times;
                            tier_no[i] = tier_no_temp;
                            state_number_chosen[i] = tier_no_temp;
                            state_weight_chosen[i] = state_weight_chosen_temp;
                        }
                    }
                }
            }
        }
        double total_objective_value =0.0;
        for(int i=0; i<objective_value.length; i++)
            total_objective_value = total_objective_value + objective_value[i] * ratio[i];
        return total_objective_value;
    }

    String[] solution_space(int s, int t, int n){
        ArrayList value = new ArrayList();
        /*
        int total_number =0;
        if(s==2){
            for(int i=t; i>=0; i--)
                for(int ii=i; ii>=0; ii--){
                    if(i+ii==n)
                        total_number++;
                }
        }
        if(s==3){
            for(int i=t; i>=0; i--)
                for(int ii=i; ii>=0; ii--)
                    for(int j=ii; j>=0; j--){
                        if(i+ii+j==n)
                            total_number++;
                }
        }
        if(s==4){
            for(int i=t; i>=0; i--)
                for(int ii=i; ii>=0; ii--)
                    for(int j=ii; j>=0; j--)
                        for(int jj=j; jj>=0; jj--){
                            if(i+ii+j+jj==n)
                                total_number++;
                }
        }
        if(s==5){
            for(int i=t; i>=0; i--)
                for(int ii=i; ii>=0; ii--)
                    for(int j=ii; j>=0; j--)
                        for(int jj=j; jj>=0; jj--)
                            for(int k=jj; k>=0; k--){
                                if(i+ii+j+jj+k==n)
                                    total_number++;
                }
        }
        if(s==6){
            for(int i=t; i>=0; i--)
                for(int ii=i; ii>=0; ii--)
                    for(int j=ii; j>=0; j--)
                        for(int jj=j; jj>=0; jj--)
                            for(int k=jj; k>=0; k--)
                                for(int kk=k; kk>=0; kk--){
                                    if(i+ii+j+jj+k+kk==n)
                                        total_number++;
                }
        }
        if(s==7){
            for(int i=t; i>=0; i--)
                for(int ii=i; ii>=0; ii--)
                    for(int j=ii; j>=0; j--)
                        for(int jj=j; jj>=0; jj--)
                            for(int k=jj; k>=0; k--)
                                for(int kk=k; kk>=0; kk--)
                                    for(int h=kk; h>=0; h--){
                                        if(i+ii+j+jj+k+kk+h==n)
                                            total_number++;
                }
        }
        if(s==8){
            for(int i=t; i>=0; i--)
                for(int ii=i; ii>=0; ii--)
                    for(int j=ii; j>=0; j--)
                        for(int jj=j; jj>=0; jj--)
                            for(int k=jj; k>=0; k--)
                                for(int kk=k; kk>=0; kk--)
                                    for(int h=kk; h>=0; h--)
                                        for(int hh=h; hh>=0; hh--){
                                            if(i+ii+j+jj+k+kk+h+hh==n)
                                                total_number++;
                }
        }

*/

        //String[] result = new String[total_number];
        //int index=0;


        if(s==2){
            for(int i=t; i>=0; i--)
                for(int ii=i; ii>=0; ii--)
                    if(i+ii==n){
                        value.add(String.valueOf(i)+String.valueOf(ii));
                        //result[index]=String.valueOf(i)+String.valueOf(ii);
                        //index++;
                    }
        }
        if(s==3){
            for(int i=t; i>=0; i--)
                for(int ii=i; ii>=0; ii--)
                    for(int j=ii; j>=0; j--){
                        if(i+ii+j==n){
                            value.add(String.valueOf(i)+String.valueOf(ii)+String.valueOf(j));
                            //result[index]=String.valueOf(i)+String.valueOf(ii)+String.valueOf(j);
                            //index++;
                        }
                }
        }
        if(s==4){
            for(int i=t; i>=0; i--)
                for(int ii=i; ii>=0; ii--)
                    for(int j=ii; j>=0; j--)
                        for(int jj=j; jj>=0; jj--){
                            if(i+ii+j+jj==n){
                                value.add(String.valueOf(i)+String.valueOf(ii)+String.valueOf(j)+String.valueOf(jj));
                                //result[index]=String.valueOf(i)+String.valueOf(ii)+String.valueOf(j)+String.valueOf(jj);
                                //index++;
                            }
                }
        }
        if(s==5){
            for(int i=t; i>=0; i--)
                for(int ii=i; ii>=0; ii--)
                    for(int j=ii; j>=0; j--)
                        for(int jj=j; jj>=0; jj--)
                            for(int k=jj; k>=0; k--){
                                if(i+ii+j+jj+k==n){
                                    value.add(String.valueOf(i)+String.valueOf(ii)+String.valueOf(j)+String.valueOf(jj)+String.valueOf(k));
                                    //result[index]=String.valueOf(i)+String.valueOf(ii)+String.valueOf(j)+String.valueOf(jj)+String.valueOf(k);
                                    //index++;
                                }
                }
        }
        if(s==6){
            for(int i=t; i>=0; i--)
                for(int ii=i; ii>=0; ii--)
                    for(int j=ii; j>=0; j--)
                        for(int jj=j; jj>=0; jj--)
                            for(int k=jj; k>=0; k--)
                                for(int kk=k; kk>=0; kk--){
                                    if(i+ii+j+jj+k+kk==n){
                                        value.add(String.valueOf(i)+String.valueOf(ii)+String.valueOf(j)+String.valueOf(jj)+String.valueOf(k)+String.valueOf(kk));
                                        //result[index]=String.valueOf(i)+String.valueOf(ii)+String.valueOf(j)+String.valueOf(jj)+String.valueOf(k)+String.valueOf(kk);
                                        //index++;
                                    }
                }
        }
        if(s==7){
            for(int i=t; i>=0; i--)
                for(int ii=i; ii>=0; ii--)
                    for(int j=ii; j>=0; j--)
                        for(int jj=j; jj>=0; jj--)
                            for(int k=jj; k>=0; k--)
                                for(int kk=k; kk>=0; kk--)
                                    for(int h=kk; h>=0; h--){
                                        if(i+ii+j+jj+k+kk+h==n){
                                            value.add(String.valueOf(i)+String.valueOf(ii)+String.valueOf(j)+String.valueOf(jj)+String.valueOf(k)+String.valueOf(kk)+String.valueOf(h));
                                            //result[index]=String.valueOf(i)+String.valueOf(ii)+String.valueOf(j)+String.valueOf(jj)+String.valueOf(k)+String.valueOf(kk)+String.valueOf(h);
                                            //index++;
                                        }
                }
        }
        if(s==8){
            for(int i=t; i>=0; i--)
                for(int ii=i; ii>=0; ii--)
                    for(int j=ii; j>=0; j--)
                        for(int jj=j; jj>=0; jj--)
                            for(int k=jj; k>=0; k--)
                                for(int kk=k; kk>=0; kk--)
                                    for(int h=kk; h>=0; h--)
                                        for(int hh=h; hh>=0; hh--){
                                            if(i+ii+j+jj+k+kk+h+hh==n){
                                                value.add(String.valueOf(i)+String.valueOf(ii)+String.valueOf(j)+String.valueOf(jj)+String.valueOf(k)+String.valueOf(kk)+String.valueOf(h)+String.valueOf(hh));
                                                //result[index]=String.valueOf(i)+String.valueOf(ii)+String.valueOf(j)+String.valueOf(jj)+String.valueOf(k)+String.valueOf(kk)+String.valueOf(h)+String.valueOf(hh);
                                                //index++;
                                            }
                }
        }
        
        String[] result = new String[value.size()];

        for(int i=0; i<value.size(); i++)
            result[i] = (String) value.get(i);

//        for(int i=0; i<result.length; i++)
//                System.out.println(result[i]);
        return result;
    }
/*
     int permutation_size(String nc, int t, int[] gn){
        int total_column=0;  //calculate the number of stacks on which there are empty slots
        for(int i=0; i<nc.length(); i++){
            //System.out.println(nc.charAt(i));
            if( nc.charAt(i) > '0')
                total_column++;
        }
        int index=0;

        if(total_column==1){
            int already_have1 = t-Integer.parseInt(String.valueOf(nc.charAt(0)));
                index=column_state(already_have1, gn).length;
        }

        if(total_column==2){
            int already_have1 = t-Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t-Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int column_state_size1 = column_state(already_have1, gn).length;
            int column_state_size2 = column_state(already_have2, gn).length;
            for(int i=0; i<column_state_size1; i++){
                int start_position2;
                if(already_have2==already_have1)
                    start_position2=i;
                else
                    start_position2=0;
                for(int j=start_position2; j<column_state_size2; j++)
                        index++;
            }
        }

        if(total_column==3){
            int already_have1 = t-Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t-Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t-Integer.parseInt(String.valueOf(nc.charAt(2)));
            int column_state_size1 = column_state(already_have1, gn).length;
            int column_state_size2 = column_state(already_have2, gn).length;
            int column_state_size3 = column_state(already_have3, gn).length;
            for(int i=0; i<column_state_size1; i++){
                int start_position2;
                if(already_have2==already_have1)
                    start_position2=i;
                else
                    start_position2=0;
                for(int ii=start_position2; ii<column_state_size2; ii++){
                    int start_position3;
                    if(already_have3==already_have2)
                        start_position3=ii;
                    else
                        start_position3=0;
                    for(int j=start_position3; j<column_state_size3; j++)
                        index++;
                }
            }
        }

        if(total_column==4){
            int already_have1 = t-Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t-Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t-Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t-Integer.parseInt(String.valueOf(nc.charAt(3)));
            int column_state_size1 = column_state(already_have1, gn).length;
            int column_state_size2 = column_state(already_have2, gn).length;
            int column_state_size3 = column_state(already_have3, gn).length;
            int column_state_size4 = column_state(already_have4, gn).length;
            for(int i=0; i<column_state_size1; i++){
                int start_position2;
                if(already_have2==already_have1)
                    start_position2=i;
                else
                    start_position2=0;
                for(int ii=start_position2; ii<column_state_size2; ii++){
                    int start_position3;
                    if(already_have3==already_have2)
                        start_position3=ii;
                    else
                        start_position3=0;
                    for(int j=start_position3; j<column_state_size3; j++){
                        int start_position4;
                        if(already_have4==already_have3)
                            start_position4=j;
                        else
                            start_position4=0;
                        for(int jj=start_position4; jj<column_state_size4; jj++)
                            index++;
                    }
                }
            }
        }

        if(total_column==5){
            int already_have1 = t-Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t-Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t-Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t-Integer.parseInt(String.valueOf(nc.charAt(3)));
            int already_have5 = t-Integer.parseInt(String.valueOf(nc.charAt(4)));
            int column_state_size1 = column_state(already_have1, gn).length;
            int column_state_size2 = column_state(already_have2, gn).length;
            int column_state_size3 = column_state(already_have3, gn).length;
            int column_state_size4 = column_state(already_have4, gn).length;
            int column_state_size5 = column_state(already_have5, gn).length;
            for(int i=0; i<column_state_size1; i++){
                int start_position2;
                if(already_have2==already_have1)
                    start_position2=i;
                else
                    start_position2=0;
                for(int ii=start_position2; ii<column_state_size2; ii++){
                    int start_position3;
                    if(already_have3==already_have2)
                        start_position3=ii;
                    else
                        start_position3=0;
                    for(int j=start_position3; j<column_state_size3; j++){
                        int start_position4;
                        if(already_have4==already_have3)
                            start_position4=j;
                        else
                            start_position4=0;
                        for(int jj=start_position4; jj<column_state_size4; jj++){
                            int start_position5;
                            if(already_have5==already_have4)
                                start_position5=jj;
                            else
                                start_position5=0;
                            for(int k=start_position5; k<column_state_size5; k++)
                                index++;
                        }
                    }
                }
            }
        }

        if(total_column==6){
            int already_have1 = t-Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t-Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t-Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t-Integer.parseInt(String.valueOf(nc.charAt(3)));
            int already_have5 = t-Integer.parseInt(String.valueOf(nc.charAt(4)));
            int already_have6 = t-Integer.parseInt(String.valueOf(nc.charAt(5)));
            int column_state_size1 = column_state(already_have1, gn).length;
            int column_state_size2 = column_state(already_have2, gn).length;
            int column_state_size3 = column_state(already_have3, gn).length;
            int column_state_size4 = column_state(already_have4, gn).length;
            int column_state_size5 = column_state(already_have5, gn).length;
            int column_state_size6 = column_state(already_have6, gn).length;
            for(int i=0; i<column_state_size1; i++){
                int start_position2;
                if(already_have2==already_have1)
                    start_position2=i;
                else
                    start_position2=0;
                for(int ii=start_position2; ii<column_state_size2; ii++){
                    int start_position3;
                    if(already_have3==already_have2)
                        start_position3=ii;
                    else
                        start_position3=0;
                    for(int j=start_position3; j<column_state_size3; j++){
                        int start_position4;
                        if(already_have4==already_have3)
                            start_position4=j;
                        else
                            start_position4=0;
                        for(int jj=start_position4; jj<column_state_size4; jj++){
                            int start_position5;
                            if(already_have5==already_have4)
                                start_position5=jj;
                            else
                                start_position5=0;
                            for(int k=start_position5; k<column_state_size5; k++){
                                int start_position6;
                                if(already_have6==already_have5)
                                    start_position6=k;
                                else
                                    start_position6=0;
                                 for(int kk=start_position6; kk<column_state_size6; kk++)
                                    index++;
                            }
                        }
                    }
                }
            }
        }

        if(total_column==7){
            int already_have1 = t-Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t-Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t-Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t-Integer.parseInt(String.valueOf(nc.charAt(3)));
            int already_have5 = t-Integer.parseInt(String.valueOf(nc.charAt(4)));
            int already_have6 = t-Integer.parseInt(String.valueOf(nc.charAt(5)));
            int already_have7 = t-Integer.parseInt(String.valueOf(nc.charAt(6)));
            int column_state_size1 = column_state(already_have1, gn).length;
            int column_state_size2 = column_state(already_have2, gn).length;
            int column_state_size3 = column_state(already_have3, gn).length;
            int column_state_size4 = column_state(already_have4, gn).length;
            int column_state_size5 = column_state(already_have5, gn).length;
            int column_state_size6 = column_state(already_have6, gn).length;
            int column_state_size7 = column_state(already_have7, gn).length;
            for(int i=0; i<column_state_size1; i++){
                int start_position2;
                if(already_have2==already_have1)
                    start_position2=i;
                else
                    start_position2=0;
                for(int ii=start_position2; ii<column_state_size2; ii++){
                    int start_position3;
                    if(already_have3==already_have2)
                        start_position3=ii;
                    else
                        start_position3=0;
                    for(int j=start_position3; j<column_state_size3; j++){
                        int start_position4;
                        if(already_have4==already_have3)
                            start_position4=j;
                        else
                            start_position4=0;
                        for(int jj=start_position4; jj<column_state_size4; jj++){
                            int start_position5;
                            if(already_have5==already_have4)
                                start_position5=jj;
                            else
                                start_position5=0;
                            for(int k=start_position5; k<column_state_size5; k++){
                                int start_position6;
                                if(already_have6==already_have5)
                                    start_position6=k;
                                else
                                    start_position6=0;
                                 for(int kk=start_position6; kk<column_state_size6; kk++){
                                     int start_position7;
                                    if(already_have7==already_have6)
                                        start_position7=kk;
                                    else
                                        start_position7=0;
                                     for(int m=start_position7; m<column_state_size7; m++)
                                        index++;
                                 }
                            }
                        }
                    }
                }
            }
        }

        if(total_column==8){
            int already_have1 = t-Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t-Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t-Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t-Integer.parseInt(String.valueOf(nc.charAt(3)));
            int already_have5 = t-Integer.parseInt(String.valueOf(nc.charAt(4)));
            int already_have6 = t-Integer.parseInt(String.valueOf(nc.charAt(5)));
            int already_have7 = t-Integer.parseInt(String.valueOf(nc.charAt(6)));
            int already_have8 = t-Integer.parseInt(String.valueOf(nc.charAt(7)));
            int column_state_size1 = column_state(already_have1, gn).length;
            int column_state_size2 = column_state(already_have2, gn).length;
            int column_state_size3 = column_state(already_have3, gn).length;
            int column_state_size4 = column_state(already_have4, gn).length;
            int column_state_size5 = column_state(already_have5, gn).length;
            int column_state_size6 = column_state(already_have6, gn).length;
            int column_state_size7 = column_state(already_have7, gn).length;
            int column_state_size8 = column_state(already_have8, gn).length;
            for(int i=0; i<column_state_size1; i++){
                int start_position2;
                if(already_have2==already_have1)
                    start_position2=i;
                else
                    start_position2=0;
                for(int ii=start_position2; ii<column_state_size2; ii++){
                    int start_position3;
                    if(already_have3==already_have2)
                        start_position3=ii;
                    else
                        start_position3=0;
                    for(int j=start_position3; j<column_state_size3; j++){
                        int start_position4;
                        if(already_have4==already_have3)
                            start_position4=j;
                        else
                            start_position4=0;
                        for(int jj=start_position4; jj<column_state_size4; jj++){
                            int start_position5;
                            if(already_have5==already_have4)
                                start_position5=jj;
                            else
                                start_position5=0;
                            for(int k=start_position5; k<column_state_size5; k++){
                                int start_position6;
                                if(already_have6==already_have5)
                                    start_position6=k;
                                else
                                    start_position6=0;
                                 for(int kk=start_position6; kk<column_state_size6; kk++){
                                     int start_position7;
                                    if(already_have7==already_have6)
                                        start_position7=kk;
                                    else
                                        start_position7=0;
                                     for(int m=start_position7; m<column_state_size7; m++){
                                         int start_position8;
                                        if(already_have8==already_have7)
                                            start_position8=m;
                                        else
                                            start_position8=0;
                                         for(int mm=start_position8; mm<column_state_size8; mm++)
                                            index++;
                                     }
                                 }
                            }
                        }
                    }
                }
            }
        }



        return index;

     }
*/
     int[][] permutation(String nc, int t, int[] gn){
        int total_column=0;  //calculate the number of stacks on which there are empty slots
        for(int i=0; i<nc.length(); i++){
            //System.out.println(nc.charAt(i));
            if( nc.charAt(i) > '0')
                total_column++;
        }

        ArrayList value = new ArrayList();
        //int permutation_size = permutation_size(nc, t, gn);
        //int[][] sc=new int[permutation_size][nc.length()];
        //int index=0;

        if(total_column==1){
            int already_have1 = t-Integer.parseInt(String.valueOf(nc.charAt(0)));
            int[] column1;
                column1 = column_state(already_have1, gn);
                for(int i=0; i<column1.length; i++){
                    value.add(column1[i]);
                    //sc[index][0]=column1[i];
                    for(int h=1; h<nc.length(); h++)
                        //sc[index][h] = t;
                        value.add(t);
                    //index++;
                }
        }

        if(total_column==2){
            int already_have1 = t-Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t-Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int[] column1 = column_state(already_have1, gn);
            int[] column2 = column_state(already_have2, gn);
            int column_state_size1 = column1.length;
            int column_state_size2 = column2.length;
            for(int i=0; i<column_state_size1; i++){
                int start_position2;
                if(already_have2==already_have1)
                    start_position2=i;
                else
                    start_position2=0;
                for(int j=start_position2; j<column_state_size2; j++){
                    value.add(column1[i]);
                    value.add(column2[j]);
                    //sc[index][0]=column1[i];
                    //sc[index][1]=column2[j];
                    for(int h=2; h<nc.length(); h++)
                        //sc[index][h] = t;
                        value.add(t);
                    //index++;
                    }
            }
        }

        if(total_column==3){
            int already_have1 = t-Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t-Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t-Integer.parseInt(String.valueOf(nc.charAt(2)));
            int[] column1 = column_state(already_have1, gn);
            int[] column2 = column_state(already_have2, gn);
            int[] column3 = column_state(already_have3, gn);
            int column_state_size1 = column1.length;
            int column_state_size2 = column2.length;
            int column_state_size3 = column3.length;
            for(int i=0; i<column_state_size1; i++){
                int start_position2;
                if(already_have2==already_have1)
                    start_position2=i;
                else
                    start_position2=0;
                for(int ii=start_position2; ii<column_state_size2; ii++){
                    int start_position3;
                    if(already_have3==already_have2)
                        start_position3=ii;
                    else
                        start_position3=0;
                    for(int j=start_position3; j<column_state_size3; j++){
                        value.add(column1[i]);
                        value.add(column2[ii]);
                        value.add(column3[j]);
                        //sc[index][0]=column1[i];
                        //sc[index][1]=column2[ii];
                        //sc[index][2]=column3[j];
                        for(int h=3; h<nc.length(); h++)
                            //sc[index][h] = t;
                            value.add(t);
                        //index++;
                    }
                }
            }
        }

         if(total_column==4){
            int already_have1 = t-Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t-Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t-Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t-Integer.parseInt(String.valueOf(nc.charAt(3)));
            int[] column1 = column_state(already_have1, gn);
            int[] column2 = column_state(already_have2, gn);
            int[] column3 = column_state(already_have3, gn);
            int[] column4 = column_state(already_have4, gn);
            int column_state_size1 = column1.length;
            int column_state_size2 = column2.length;
            int column_state_size3 = column3.length;
            int column_state_size4 = column4.length;
            for(int i=0; i<column_state_size1; i++){
                int start_position2;
                if(already_have2==already_have1)
                    start_position2=i;
                else
                    start_position2=0;
                for(int ii=start_position2; ii<column_state_size2; ii++){
                    int start_position3;
                    if(already_have3==already_have2)
                        start_position3=ii;
                    else
                        start_position3=0;
                    for(int j=start_position3; j<column_state_size3; j++){
                        int start_position4;
                        if(already_have4==already_have3)
                            start_position4=j;
                        else
                            start_position4=0;
                        for(int jj=start_position4; jj<column_state_size4; jj++){
                            value.add(column1[i]);
                            value.add(column2[ii]);
                            value.add(column3[j]);
                            value.add(column4[jj]);
                            //sc[index][0]=column1[i];
                            //sc[index][1]=column2[ii];
                            //sc[index][2]=column3[j];
                            //sc[index][3]=column4[jj];
                            for(int h=4; h<nc.length(); h++)
                                //sc[index][h] = t;
                                value.add(t);
                            //index++;
                        }
                    }
                }
            }
        }

        if(total_column==5){
            int already_have1 = t-Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t-Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t-Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t-Integer.parseInt(String.valueOf(nc.charAt(3)));
            int already_have5 = t-Integer.parseInt(String.valueOf(nc.charAt(4)));
            int[] column1 = column_state(already_have1, gn);
            int[] column2 = column_state(already_have2, gn);
            int[] column3 = column_state(already_have3, gn);
            int[] column4 = column_state(already_have4, gn);
            int[] column5 = column_state(already_have5, gn);
            int column_state_size1 = column1.length;
            int column_state_size2 = column2.length;
            int column_state_size3 = column3.length;
            int column_state_size4 = column4.length;
            int column_state_size5 = column5.length;
            for(int i=0; i<column_state_size1; i++){
                int start_position2;
                if(already_have2==already_have1)
                    start_position2=i;
                else
                    start_position2=0;
                for(int ii=start_position2; ii<column_state_size2; ii++){
                    int start_position3;
                    if(already_have3==already_have2)
                        start_position3=ii;
                    else
                        start_position3=0;
                    for(int j=start_position3; j<column_state_size3; j++){
                        int start_position4;
                        if(already_have4==already_have3)
                            start_position4=j;
                        else
                            start_position4=0;
                        for(int jj=start_position4; jj<column_state_size4; jj++){
                            int start_position5;
                            if(already_have5==already_have4)
                                start_position5=jj;
                            else
                                start_position5=0;
                            for(int k=start_position5; k<column_state_size5; k++){
                                value.add(column1[i]);
                                value.add(column2[ii]);
                                value.add(column3[j]);
                                value.add(column4[jj]);
                                value.add(column5[k]);
                                //sc[index][0]=column1[i];
                                //sc[index][1]=column2[ii];
                                //sc[index][2]=column3[j];
                                //sc[index][3]=column4[jj];
                                //sc[index][4]=column5[k];
                                for(int h=5; h<nc.length(); h++)
                                    //sc[index][h] = t;
                                    value.add(t);
                                //index++;
                            }
                        }
                    }
                }
            }
        }

        if(total_column==6){
            int already_have1 = t-Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t-Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t-Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t-Integer.parseInt(String.valueOf(nc.charAt(3)));
            int already_have5 = t-Integer.parseInt(String.valueOf(nc.charAt(4)));
            int already_have6 = t-Integer.parseInt(String.valueOf(nc.charAt(5)));
            int[] column1 = column_state(already_have1, gn);
            int[] column2 = column_state(already_have2, gn);
            int[] column3 = column_state(already_have3, gn);
            int[] column4 = column_state(already_have4, gn);
            int[] column5 = column_state(already_have5, gn);
            int[] column6 = column_state(already_have6, gn);
            int column_state_size1 = column1.length;
            int column_state_size2 = column2.length;
            int column_state_size3 = column3.length;
            int column_state_size4 = column4.length;
            int column_state_size5 = column5.length;
            int column_state_size6 = column6.length;
            for(int i=0; i<column_state_size1; i++){
                int start_position2;
                if(already_have2==already_have1)
                    start_position2=i;
                else
                    start_position2=0;
                for(int ii=start_position2; ii<column_state_size2; ii++){
                    int start_position3;
                    if(already_have3==already_have2)
                        start_position3=ii;
                    else
                        start_position3=0;
                    for(int j=start_position3; j<column_state_size3; j++){
                        int start_position4;
                        if(already_have4==already_have3)
                            start_position4=j;
                        else
                            start_position4=0;
                        for(int jj=start_position4; jj<column_state_size4; jj++){
                            int start_position5;
                            if(already_have5==already_have4)
                                start_position5=jj;
                            else
                                start_position5=0;
                            for(int k=start_position5; k<column_state_size5; k++){
                                int start_position6;
                                if(already_have6==already_have5)
                                    start_position6=k;
                                else
                                    start_position6=0;
                                 for(int kk=start_position6; kk<column_state_size6; kk++){
                                     value.add(column1[i]);
                                    value.add(column2[ii]);
                                    value.add(column3[j]);
                                    value.add(column4[jj]);
                                    value.add(column5[k]);
                                    value.add(column6[kk]);
                                     //sc[index][0]=column1[i];
                                    //sc[index][1]=column2[ii];
                                    //sc[index][2]=column3[j];
                                    //sc[index][3]=column4[jj];
                                    //sc[index][4]=column5[k];
                                    //sc[index][5]=column6[kk];
                                    for(int h=6; h<nc.length(); h++)
                                        //sc[index][h] = t;
                                        value.add(t);
                                    //index++;
                                 }
                            }
                        }
                    }
                }
            }
        }

        if(total_column==7){
            int already_have1 = t-Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t-Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t-Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t-Integer.parseInt(String.valueOf(nc.charAt(3)));
            int already_have5 = t-Integer.parseInt(String.valueOf(nc.charAt(4)));
            int already_have6 = t-Integer.parseInt(String.valueOf(nc.charAt(5)));
            int already_have7 = t-Integer.parseInt(String.valueOf(nc.charAt(6)));
            int[] column1 = column_state(already_have1, gn);
            int[] column2 = column_state(already_have2, gn);
            int[] column3 = column_state(already_have3, gn);
            int[] column4 = column_state(already_have4, gn);
            int[] column5 = column_state(already_have5, gn);
            int[] column6 = column_state(already_have6, gn);
            int[] column7 = column_state(already_have7, gn);
            int column_state_size1 = column1.length;
            int column_state_size2 = column2.length;
            int column_state_size3 = column3.length;
            int column_state_size4 = column4.length;
            int column_state_size5 = column5.length;
            int column_state_size6 = column6.length;
            int column_state_size7 = column7.length;
            for(int i=0; i<column_state_size1; i++){
                int start_position2;
                if(already_have2==already_have1)
                    start_position2=i;
                else
                    start_position2=0;
                for(int ii=start_position2; ii<column_state_size2; ii++){
                    int start_position3;
                    if(already_have3==already_have2)
                        start_position3=ii;
                    else
                        start_position3=0;
                    for(int j=start_position3; j<column_state_size3; j++){
                        int start_position4;
                        if(already_have4==already_have3)
                            start_position4=j;
                        else
                            start_position4=0;
                        for(int jj=start_position4; jj<column_state_size4; jj++){
                            int start_position5;
                            if(already_have5==already_have4)
                                start_position5=jj;
                            else
                                start_position5=0;
                            for(int k=start_position5; k<column_state_size5; k++){
                                int start_position6;
                                if(already_have6==already_have5)
                                    start_position6=k;
                                else
                                    start_position6=0;
                                 for(int kk=start_position6; kk<column_state_size6; kk++){
                                     int start_position7;
                                    if(already_have7==already_have6)
                                        start_position7=kk;
                                    else
                                        start_position7=0;
                                     for(int m=start_position7; m<column_state_size7; m++){
                                         value.add(column1[i]);
                                        value.add(column2[ii]);
                                        value.add(column3[j]);
                                        value.add(column4[jj]);
                                        value.add(column5[k]);
                                        value.add(column6[kk]);
                                        value.add(column7[m]);
                                         //sc[index][0]=column1[i];
                                        //sc[index][1]=column2[ii];
                                        //sc[index][2]=column3[j];
                                        //sc[index][3]=column4[jj];
                                        //sc[index][4]=column5[k];
                                        //sc[index][5]=column6[kk];
                                        //sc[index][6]=column7[m];
                                        for(int h=7; h<nc.length(); h++)
                                            //sc[index][h] = t;
                                            value.add(t);
                                        //index++;
                                     }
                                 }
                            }
                        }
                    }
                }
            }
        }

        if(total_column==8){
            int already_have1 = t-Integer.parseInt(String.valueOf(nc.charAt(0))); //Integer.parseInt(String.valueOf(nc.charAt(0))): the number of empty slot in a stack
            int already_have2 = t-Integer.parseInt(String.valueOf(nc.charAt(1))); // already_have1: the number of containers stacking on the stack.
            int already_have3 = t-Integer.parseInt(String.valueOf(nc.charAt(2)));
            int already_have4 = t-Integer.parseInt(String.valueOf(nc.charAt(3)));
            int already_have5 = t-Integer.parseInt(String.valueOf(nc.charAt(4)));
            int already_have6 = t-Integer.parseInt(String.valueOf(nc.charAt(5)));
            int already_have7 = t-Integer.parseInt(String.valueOf(nc.charAt(6)));
            int already_have8 = t-Integer.parseInt(String.valueOf(nc.charAt(7)));
            int[] column1 = column_state(already_have1, gn);
            int[] column2 = column_state(already_have2, gn);
            int[] column3 = column_state(already_have3, gn);
            int[] column4 = column_state(already_have4, gn);
            int[] column5 = column_state(already_have5, gn);
            int[] column6 = column_state(already_have6, gn);
            int[] column7 = column_state(already_have7, gn);
            int[] column8 = column_state(already_have8, gn);
            int column_state_size1 = column1.length;
            int column_state_size2 = column2.length;
            int column_state_size3 = column3.length;
            int column_state_size4 = column4.length;
            int column_state_size5 = column5.length;
            int column_state_size6 = column6.length;
            int column_state_size7 = column7.length;
            int column_state_size8 = column8.length;
            for(int i=0; i<column_state_size1; i++){
                int start_position2;
                if(already_have2==already_have1)
                    start_position2=i;
                else
                    start_position2=0;
                for(int ii=start_position2; ii<column_state_size2; ii++){
                    int start_position3;
                    if(already_have3==already_have2)
                        start_position3=ii;
                    else
                        start_position3=0;
                    for(int j=start_position3; j<column_state_size3; j++){
                        int start_position4;
                        if(already_have4==already_have3)
                            start_position4=j;
                        else
                            start_position4=0;
                        for(int jj=start_position4; jj<column_state_size4; jj++){
                            int start_position5;
                            if(already_have5==already_have4)
                                start_position5=jj;
                            else
                                start_position5=0;
                            for(int k=start_position5; k<column_state_size5; k++){
                                int start_position6;
                                if(already_have6==already_have5)
                                    start_position6=k;
                                else
                                    start_position6=0;
                                 for(int kk=start_position6; kk<column_state_size6; kk++){
                                     int start_position7;
                                    if(already_have7==already_have6)
                                        start_position7=kk;
                                    else
                                        start_position7=0;
                                     for(int m=start_position7; m<column_state_size7; m++){
                                         int start_position8;
                                        if(already_have8==already_have7)
                                            start_position8=m;
                                        else
                                            start_position8=0;
                                         for(int mm=start_position8; mm<column_state_size8; mm++){
                                             value.add(column1[i]);
                                             value.add(column2[ii]);
                                            value.add(column3[j]);
                                            value.add(column4[jj]);
                                            value.add(column5[k]);
                                            value.add(column6[kk]);
                                            value.add(column7[m]);
                                            value.add(column8[mm]);
                                             //sc[index][0]=column1[i];
                                            //sc[index][1]=column2[ii];
                                            //sc[index][2]=column3[j];
                                            //sc[index][3]=column4[jj];
                                            //sc[index][4]=column5[k];
                                            //sc[index][5]=column6[kk];
                                            //sc[index][6]=column7[m];
                                            //sc[index][7]=column8[mm];
                                            for(int h=8; h<nc.length(); h++)
                                                //sc[index][h] = t;
                                                value.add(t);
                                            //index++;
                                         }
                                     }
                                 }
                            }
                        }
                    }
                }
            }
        }

        int[][] sc=new int[value.size()/nc.length()][nc.length()];
        for(int i=0; i<value.size()/nc.length(); i++)
            for(int j=0; j<nc.length(); j++)
                //value.
                sc[i][j]=(Integer) value.get(i*nc.length()+j);

        
        //int index=0;

       // for(int i=0; i< permutation_size; i++)
          //  for(int j=0; j<nc.length(); j++)
           //     System.out.println(i+" "+j+" "+sc[i][j]);

        return sc;

     }

    int[] column_state(int column_number, int[] gn){
        ArrayList value = new ArrayList();
        //Vector value = new Vector();
        /*
        int total_number =0;
        if(gn.length==2){
            for(int i=column_number; i>=0; i--)
                for(int ii=column_number; ii>=0; ii--){
                    if(i+ii==column_number)
                        total_number++;
                }
        }
        if(gn.length==3){
            for(int i=column_number; i>=0; i--)
                for(int ii=column_number; ii>=0; ii--)
                    for(int j=column_number; j>=0; j--){
                        if(i+ii+j==column_number)
                            total_number++;
                }
        }
        if(gn.length==4){
            for(int i=column_number; i>=0; i--)
                for(int ii=column_number; ii>=0; ii--)
                    for(int j=column_number; j>=0; j--)
                        for(int jj=column_number; jj>=0; jj--){
                            if(i+ii+j+jj==column_number)
                                total_number++;
                }
        }

        int[] result = new int[total_number];
        int index=0;
*/
        if(gn.length==2){
            for(int i=column_number; i>=0; i--)
                for(int ii=column_number; ii>=0; ii--){
                    if(i+ii==column_number){
                        value.add(i*gn[0]+ii*gn[1]);
                        //result[index]=i*gn[0]+ii*gn[1];
                        //index++;
                    }
                }
        }
        if(gn.length==3){
            for(int i=column_number; i>=0; i--)
                for(int ii=column_number; ii>=0; ii--)
                    for(int j=column_number; j>=0; j--){
                        if(i+ii+j==column_number){
                            value.add(i*gn[0]+ii*gn[1]+j*gn[2]);
                            //result[index]=i*gn[0]+ii*gn[1]+j*gn[2];
                            //index++;
                        }
                }
        }
        if(gn.length==4){
            for(int i=column_number; i>=0; i--)
                for(int ii=column_number; ii>=0; ii--)
                    for(int j=column_number; j>=0; j--)
                        for(int jj=column_number; jj>=0; jj--){
                            if(i+ii+j+jj==column_number){
                                value.add(i*gn[0]+ii*gn[1]+j*gn[2]+jj*gn[3]);
                                //result[index]=i*gn[0]+ii*gn[1]+j*gn[2]+jj*gn[3];
                                //index++;
                            }
                    }
        }

        int[] result = new int[value.size()];
        for(int i=0; i<value.size(); i++)
            result[i]=  (Integer) value.get(i);
        
        return result;


    }

/*
    int dimension_size(int s, int t, int n, int[] gn, String[] result){
        int total_number=0;
        for(int i=0; i<result.length; i++){
            total_number = total_number + permutation_size(result[i], t, gn);
        }
        return total_number;
    }
*/
    void dynamic_programming(int s, int t, int[] gn, double[] ratio){
        // s: the number of stacks
        // t: the number of tiers
        // g: the number of weight groups
        int n=0;
        String[] nc_pre;
        int[][] sc_pre;
        double[] va_pre;
        int tn_pre;
        //String[] nc_cur;
        //int[][] sc_cur;
        //double[] va_cur;
        //int tn_cur;

        tn_pre=1;
        nc_pre = new String[tn_pre];
        sc_pre = new int[tn_pre][s];
        va_pre = new double[tn_pre];

        String temp = "";
        for(int j=0; j<s; j++)
            temp = temp + "0";
        nc_pre[0] = temp;
        for(int j=0; j<s; j++)
            sc_pre[0][j] = t;
        va_pre[0] = 0.0;
        state_size= 0;
        while(n<s*t){
            n = n + 1;
            //the total dimensiion for the case when the number of empty slots equals n
            String[] nc = solution_space(s, t, n);
            ArrayList values = new ArrayList();
            //tn_cur = dimension_size(s, t,  n, gn, nc);
            //state_size=state_size+tn_cur;
            //nc_cur = new String[tn_cur];
            //sc_cur = new int[tn_cur][s];
            //va_cur = new double[tn_cur];
            //int index=0;
            for(int i=0; i<nc.length; i++){
                int[][] sc = permutation(nc[i], t, gn);
                for(int j=0; j<sc.length; j++){
                    values.add(nc[i]);
                    for(int k=0; k<sc[j].length; k++)
                        values.add(sc[j][k]);
                    double finalvalue = calculate_value(t, nc[i], sc[j], gn, nc_pre, sc_pre, va_pre, ratio);
                    values.add(finalvalue);
                   //nc_cur[index] = nc[i];
                   //sc_cur[index] = sc[j];
                   //va_cur[index] = calculate_value(t, nc[i], sc[j], gn, nc_pre, sc_pre, va_pre, ratio);
                   if(n==s*t)
                    final_value =finalvalue;
                   //index++;
                }
            }


            tn_pre=values.size()/(s+2);
            state_size=state_size+tn_pre;
            nc_pre = new String[tn_pre];
            sc_pre = new int[tn_pre][s];
            va_pre = new double[tn_pre];
            for(int i=0; i<tn_pre; i++){
                nc_pre[i] = (String) values.get(i*(s+2));
                for(int k=1; k<=s; k++)
                    sc_pre[i][k-1] = (Integer) values.get(i*(s+2)+k);
                va_pre[i] = (Double) values.get(i*(s+2)+s+1);
            }

            for(int i=0; i<nc_pre.length; i++ ){
                System.out.println(i +"  " + nc_pre[i] + " "+ sc_pre[i][0]+ " "+ sc_pre[i][1]+ " "+ va_pre[i]);
            }

        }


    }

   void dynamic_programming_simplified(int s, int t, int[] gn, double[] ratio, boolean input){
        // s: the number of stacks
        // t: the number of tiers
        // g: the number of weight groups
        int n=0;
        String[] nc_pre;
        int[][] sc_pre;
        double[] va_pre;
        int tn_pre;
        //String[] nc_cur;
        //int[][] sc_cur;
        //double[] va_cur;
        //int tn_cur;

        tn_pre=1;
        nc_pre = new String[tn_pre];
        sc_pre = new int[tn_pre][s];
        va_pre = new double[tn_pre];

        String temp = "";
        for(int j=0; j<s; j++)
            temp = temp + "0";
        nc_pre[0] = temp;
        for(int j=0; j<s; j++)
            sc_pre[0][j] = t;
        va_pre[0] = 0.0;
        state_size= 0;

        myproperties p = new myproperties();
        try{
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "store_value"+String.valueOf(s)+String.valueOf(t)+String.valueOf(gn.length)+".txt",false);
            PrintWriter write = new PrintWriter(fw);
            while(n<s*t){
                n = n + 1;
                //the total dimensiion for the case when the number of empty slots equals n
                String[] nc = solution_space(s, t, n);
                ArrayList values = new ArrayList();
                //tn_cur = dimension_size(s, t,  n, gn, nc);
                //state_size=state_size+tn_cur;
                //nc_cur = new String[tn_cur];
                //sc_cur = new int[tn_cur][s];
                //va_cur = new double[tn_cur];
                //int index=0;
                for(int i=0; i<nc.length; i++){
                    int[][] sc = permutation(nc[i], t, gn);
                    for(int j=0; j<sc.length; j++){
                        values.add(nc[i]);
                        for(int k=0; k<sc[j].length; k++)
                            values.add(sc[j][k]);
                        double finalvalue = calculate_value(t, nc[i], sc[j], gn, nc_pre, sc_pre, va_pre, ratio);
                        values.add(finalvalue);
                       //nc_cur[index] = nc[i];
                       //sc_cur[index] = sc[j];
                       //va_cur[index] = calculate_value(t, nc[i], sc[j], gn, nc_pre, sc_pre, va_pre, ratio);
                       if(n==s*t)
                        final_value =finalvalue;
                       //index++;


                        String sc_temp = "";
                       for(int m=0; m<s; m++)
                           sc_temp=sc_temp+sc[j][m];
                        write.print(nc[i]+sc_temp+"\t");
                       for(int k=0; k<gn.length; k++){
                           String next_state_weight_temp="";
                           for(int m=0; m<s; m++)
                               next_state_weight_temp = next_state_weight_temp+next_state_weight[k][m];
                           write.print(next_state_number[k]+next_state_weight_temp+"\t");
                           write.print(state_number_chosen[k]+"\t");
                           write.print(state_weight_chosen[k]+"\t");
                       }
                       write.println();
                    }
                }


                tn_pre=values.size()/(s+2);
                state_size=state_size+tn_pre;
                nc_pre = new String[tn_pre];
                sc_pre = new int[tn_pre][s];
                va_pre = new double[tn_pre];
                for(int i=0; i<tn_pre; i++){
                    nc_pre[i] = (String) values.get(i*(s+2));
                    for(int k=1; k<=s; k++)
                        sc_pre[i][k-1] = (Integer) values.get(i*(s+2)+k);
                    va_pre[i] = (Double) values.get(i*(s+2)+s+1);
                }


                for(int i=0; i<nc_pre.length; i++ ){
                    System.out.print(i +"  " + nc_pre[i] + " ");
                    for(int k=0; k<s; k++)
                        System.out.print(sc_pre[i][k]+ " ");
                    System.out.println(va_pre[i]);
                }

            }
            fw.close();
            write.close();
            }catch(IOException f){
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("error", f.toString());
            }


    }

    void dynamic_programming(int s, int t, int[] gn, double[] ratio, boolean input){
        // s: the number of stacks
        // t: the number of tiers
        // g: the number of weight groups
        int n=0;
        String[] nc_pre;
        int[][] sc_pre;
        double[] va_pre;
        int tn_pre;
        //String[] nc_cur;
        //int[][] sc_cur;
        //double[] va_cur;
        //int tn_cur;

        tn_pre=1;
        nc_pre = new String[tn_pre];
        sc_pre = new int[tn_pre][s];
        va_pre = new double[tn_pre];

        String temp = "";
        for(int j=0; j<s; j++)
            temp = temp + "0";
        nc_pre[0] = temp;
        for(int j=0; j<s; j++)
            sc_pre[0][j] = t;
        va_pre[0] = 0.0;
        state_size= 0;

        myproperties p = new myproperties();
        try{
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "store_value"+String.valueOf(s)+String.valueOf(t)+".txt",false);
            PrintWriter write = new PrintWriter(fw);
            while(n<s*t){
                n = n + 1;
                //the total dimensiion for the case when the number of empty slots equals n
                String[] nc = solution_space(s, t, n);
                ArrayList values = new ArrayList();
                //tn_cur = dimension_size(s, t,  n, gn, nc);
                //state_size=state_size+tn_cur;
                //nc_cur = new String[tn_cur];
                //sc_cur = new int[tn_cur][s];
                //va_cur = new double[tn_cur];
                //int index=0;
                for(int i=0; i<nc.length; i++){
                    int[][] sc = permutation(nc[i], t, gn);
                    for(int j=0; j<sc.length; j++){
                        values.add(nc[i]);
                        for(int k=0; k<sc[j].length; k++)
                            values.add(sc[j][k]);
                        double finalvalue = calculate_value(t, nc[i], sc[j], gn, nc_pre, sc_pre, va_pre, ratio);
                        values.add(finalvalue);
                       //nc_cur[index] = nc[i];
                       //sc_cur[index] = sc[j];
                       //va_cur[index] = calculate_value(t, nc[i], sc[j], gn, nc_pre, sc_pre, va_pre, ratio);
                       if(n==s*t)
                        final_value =finalvalue;
                       //index++;

                        write.print(nc[i]+"\t");
                       for(int m=0; m<s; m++)
                           write.print(sc[j][m]+"\t");
                       write.print(finalvalue+"\t");
                       for(int k=0; k<gn.length; k++){
                           write.print(next_weight_group[k]+"\t"+next_state_number[k]+"\t");
                           for(int m=0; m<s; m++)
                               write.print(next_state_weight[k][m]+"\t");
                           write.print(next_value[k]+"\t");
                           write.print(tier_no[k]+"\t");
                           write.print(state_number_chosen[k]+"\t");
                           write.print(state_weight_chosen[k]+"\t");
                       }
                       write.println();
                    }
                }


                tn_pre=values.size()/(s+2);
                state_size=state_size+tn_pre;
                nc_pre = new String[tn_pre];
                sc_pre = new int[tn_pre][s];
                va_pre = new double[tn_pre];
                for(int i=0; i<tn_pre; i++){
                    nc_pre[i] = (String) values.get(i*(s+2));
                    for(int k=1; k<=s; k++)
                        sc_pre[i][k-1] = (Integer) values.get(i*(s+2)+k);
                    va_pre[i] = (Double) values.get(i*(s+2)+s+1);
                }


                for(int i=0; i<nc_pre.length; i++ ){
                    System.out.println(i +"  " + nc_pre[i] + " "+ sc_pre[i][0]+ " "+ sc_pre[i][1]+ " "+ va_pre[i]);
                }

            }
            fw.close();
            write.close();
            }catch(IOException f){
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("error", f.toString());
            }


    }

  
    
}

//using a dynamic indicator
class Evaluating_simulation{
    int total_evaluation_value;
    int total_sequence_size;
    //calculate the expected addition number of relocations
    double[][][] expected_addition(int s, int t){
        double[][][] addition_relocation = new double[s*t][t+1][s*t];
        for(int i=0; i<s*t; i++)
            for(int j=0; j<t+1; j++)
                for(int k=0; k<s*t; k++)
                    addition_relocation[i][j][k] = 0.0;
        for(int i=0; i<s*t; i++){
            int cur_N = i+1; // The current N
            if(cur_N>t){ //When the current N is larger than t-1, the resutls will be meaningful.
                for(int j=0; j<t+1; j++){
                    int cur_k = j+1;
                    int number_already_moved = s*t - cur_N;
                    if(cur_k==1){ //for the case the number of empty slots is equal to 1.
                        for(int k=0; k<s*t; k++){
                            int cur_n = k+1;
                            cur_n = cur_n - number_already_moved;
                            if(cur_n > 0){
                                double temp = (double) (cur_N - (t + 1 - cur_k) - (cur_n - 1)) / (double) (cur_N - (t + 1 - cur_k));
                                if(temp > 0)
                                    addition_relocation[i][j][k] = temp;
                            }
                        }
                    }
                    if(cur_k>1){//for the case the number of empty slots is larger than 1.
                       for(int k=0; k<s*t; k++){
                            int cur_n = k+1;
                            cur_n = cur_n - number_already_moved;
                            if(cur_n > 0){
                                double part1 = 0.0;
                                for(int h=0; h<k-1; h++){
                                    part1 = part1 + addition_relocation[i][j-1][h] / (double) (cur_N - (t + 1 - cur_k));
                                }
                                double part2 = 0.0;
                                part2 = (double) (cur_N - (t + 1 - cur_k) - (cur_n - 1)) * (1 + addition_relocation[i][j-1][k])/ (cur_N - (t + 1 - cur_k));
                                addition_relocation[i][j][k] = part1 + part2;
                                //addition_relocation[i][j][k] = (cur_N - (t - cur_k) - (cur_n - 1))/ (cur_N - (t - cur_k));
                            }
                        }
                    }
                }
            }
        }
        for(int i=0; i<s*t; i++)
            for(int j=0; j<t+1; j++)
                for(int k=0; k<s*t; k++)
                    System.out.println(i+" "+j+" "+k+" "+addition_relocation[i][j][k]);
        return addition_relocation;
    }

    //calculate the rehandling number   
    void calculate_rehandle(int s, int t, String[] gn){
        Calculate_total_rehandle_prepare dataarray = new Calculate_total_rehandle_prepare();
        dataarray.datainput_simplified("store_value"+String.valueOf(s)+String.valueOf(t)+String.valueOf(gn.length)+".txt", s, gn);
        //for(int i=0; i<dataarray.size; i++)
        //System.out.println(i+" "+dataarray.next_value[i][1]);

        String[][] string_generate = dataarray.generate_string(s, t, gn);
        total_sequence_size = string_generate.length;
        int[] total_rehandle = new int[string_generate.length];
        for(int i=0; i<total_rehandle.length; i++)
            total_rehandle[i]=0;
        int final_total_rehandle =0;

        long begin_time8, end_time8, duration8;
        Date mydate3 = new Date();
        begin_time8 = mydate3.getTime();
        //incur the expected_addition routine.
        double[][][] expected_addition = expected_addition(s, t);
        for(int i=0; i<string_generate.length; i++){
        //if(i==35){
            String[] target = string_generate[i];
            int[][] bay_state = new int[s][2]; //used to describe the state of the bay
            for(int k=0; k<s; k++){
                bay_state[k][0]=t; //initialize bay_state
                bay_state[k][1]=0;
            }
            int[][] position = new int[target.length][4]; // used to describe the positions of the containers
            String cur_nc="";
            String cur_sc= "";
            //the initial state
            for(int k=0; k<s; k++){
                cur_nc=cur_nc+ String.valueOf(t);
                cur_sc=cur_sc+String.valueOf(0);
            }
            cur_nc=cur_nc+cur_sc;
            int start_index = dataarray.size-1;
            //count the number of containers for each kind of weight group
            int[] number_each_weight = new int[gn.length];
            for(int h=0; h<gn.length; h++)
                number_each_weight[h] = 0;
            for(int j=0; j<target.length; j++){
                for(int h=0; h<gn.length; h++){
                    if(target[j].equals(gn[h]))
                        number_each_weight[h] = number_each_weight[h] + 1;
                }
            }
            for(int h=1; h<gn.length; h++)
                number_each_weight[h] = number_each_weight[h] + number_each_weight[h-1]; //number_each_weight stores the cumulative number
            for(int h=gn.length-1; h>0; h--)
                number_each_weight[h] = number_each_weight[h-1] + 1; //record the starting number, i.e., the total number of containers of the previous weight group.
            number_each_weight[0] = 1;

            for(int j=0; j<target.length;j++){
                int target_index=0;
                //find the target index
                for(int k=start_index; k>=0; k--){
                    if(cur_nc.equals(dataarray.current_state[k][0])){
                            target_index=k;
                            start_index = k;
                            break;
                    }
                }
                //find the position of the target weight group
                int target_k=0;
                for(int k=0; k<gn.length; k++){
                    if(gn[k].equals(target[j])){
                        target_k=k;
                        break;
                    }
                }
                cur_nc = dataarray.next_state[target_index][target_k];

                int stack_no = 0;
                for(int k=0; k<s; k++){
                    if(bay_state[k][0]==dataarray.state_number_chosen[target_index][target_k])
                        if(bay_state[k][1]==dataarray.state_weight_chosen[target_index][target_k]){
                            //if(Math.random()<0.5){
                                stack_no = k;
                                break;
                            //}
                            //if(k==s-1)
                             //   k=0;
                        }
                }
                bay_state[stack_no][0] = bay_state[stack_no][0]-1;
                if(bay_state[stack_no][0]==0)
                    bay_state[stack_no][1] = t;
                else{
                    if(target_k==gn.length-1)
                        bay_state[stack_no][1] = bay_state[stack_no][1] + 1;
                    if(target_k==gn.length-2)
                        bay_state[stack_no][1] = bay_state[stack_no][1] + 10;
                    if(target_k==gn.length-3)
                        bay_state[stack_no][1] = bay_state[stack_no][1] + 100;
                    if(target_k==gn.length-4)
                        bay_state[stack_no][1] = bay_state[stack_no][1] + 1000;
                }




                if(target_k==gn.length-1){
                    position[j][0]=1;                    
                }
                if(target_k==gn.length-2){
                    position[j][0]=10;                    
                }
                if(target_k==gn.length-3){
                    position[j][0]=100;
                }
                if(target_k==gn.length-4){
                    position[j][0]=1000;
                }
                position[j][1] = stack_no;
                position[j][2] = dataarray.state_number_chosen[target_index][target_k];
                //position[j][3] = number_each_weight[target_k];
                //number_each_weight[target_k] = number_each_weight[target_k] -1;
                
            }
            for(int h=0; h<t; h++)
                for(int k=0; k<s; k++)
                    for(int j=0; j<target.length;j++){
                        if(position[j][1] == k && position[j][2] == h+1){
                            int weight_index = 0;
                            for(int v=0; v<gn.length; v++)
                                if(String.valueOf(position[j][0]).equals(gn[v]))
                                    weight_index = v;
                            position[j][3] = number_each_weight[weight_index];
                            number_each_weight[weight_index] = number_each_weight[weight_index] + 1;
                        }
                
            }
            total_rehandle[i] = evaluate_objective(s,t,position,expected_addition);
            System.out.print(i+" ");
            for(int h=0; h<target.length; h++){
                System.out.print(target[h]+" ");
            }
            System.out.println(total_rehandle[i]);
            //System.out.println();
            final_total_rehandle = final_total_rehandle+total_rehandle[i];
        }
        Date mydate4 = new Date();
        end_time8 = mydate4.getTime();
        duration8 = end_time8 - begin_time8;
        System.out.println("total rehandle times for the dynamic indicator:   "+final_total_rehandle);
        total_evaluation_value = final_total_rehandle;
        myproperties p = new myproperties();
        try{
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "indictor_total_rehandel_times"+String.valueOf(s)+String.valueOf(t)+String.valueOf(gn.length)+".txt",false);
            PrintWriter write = new PrintWriter(fw);
            write.println(s+"\t"+t+"\t"+string_generate.length+"\t"+final_total_rehandle+"\t"+duration8 +"\t");
             fw.close();
            write.close();
            }catch(IOException f){
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("error", f.toString());
            }
    }

    int evaluate_objective(int s, int t, int[][] position, double[][][] expected_addition){
        int rehandle_number = 0;  // The number of realized relocations which the objective. 

        // The state of the bay.
        int[][] bay_state_simulation = new int[s][2];
        for(int i=0; i<s; i++){
            bay_state_simulation[i][0] = 1;  //record the number of empty slots in each stack. By default, the average height of each stack is set as t+1.
            bay_state_simulation[i][1] = position.length;  //record the highest priority of the containter stacked in each stack.
        }
        for(int i=0; i<position.length; i++){
            for(int j=0; j<s; j++){
                if(position[i][1] == j && bay_state_simulation[j][1] > position[i][3])
                    bay_state_simulation[j][1] = position[i][3];
            }
        }
        for(int i=0; i<position.length; i++){
            int cur_no = i+1; // the target container
            int target_stack_no = 0;
            int target_tier_no = 0;
            for(int j=0; j<position.length; j++){
                if(position[j][3] == cur_no){
                    target_stack_no = position[j][1];
                    target_tier_no = position[j][2];
                }
            }
            int target_tier_total = bay_state_simulation[target_stack_no][0]; //the highest tier of the target stack. The tier no is increasing from top to bottom.
            if(target_tier_total < target_tier_no){//this means there are containers needed to be relocated.
               for(int k = target_tier_no - target_tier_total; k>0; k--){//relocate the containers sequentially from top to the one just stacked on the target one.
                   int relocated_index = 0; //record the index of the container to be relocated.
                   for(int j=0; j<position.length; j++){
                        if(position[j][1] == target_stack_no && position[j][2] == target_tier_no - k)
                            relocated_index = j;
                    }
                   
                   //find the stack with the minimum objective value.
                   double objective_temp = Double.MAX_VALUE;
                   int stack_chosen_temp = 0;
                   for(int j=0; j<s; j++){
                       if(j != target_stack_no && bay_state_simulation[j][0] > 0 ){ // bay_state_simulation[j][0] > 0 means the stack has at least one empty slot.
                        double the_previous = expected_addition[position.length - cur_no][bay_state_simulation[j][0] - 1][bay_state_simulation[j][1] - 1];
                        double the_new;
                        if(bay_state_simulation[j][0] == 1){ // the case that when the relocated container is stacked this this stack, and then this empty is full.
                            if(position[relocated_index][3] < bay_state_simulation[j][1] )
                                the_new = 0.0;
                            else
                                the_new = 1.0;
                        }
                        else{
                            if(position[relocated_index][3] < bay_state_simulation[j][1] )
                                the_new = expected_addition[position.length - cur_no][bay_state_simulation[j][0] - 1 - 1][position[relocated_index][3] - 1];
                            else
                                the_new = 1.0 + expected_addition[position.length - cur_no][bay_state_simulation[j][0] - 1 - 1][bay_state_simulation[j][1] - 1];
                        }
                        if(objective_temp > the_new - the_previous ){
                            objective_temp = the_new - the_previous;
                            stack_chosen_temp = j;
                        }
                       }
                   }
                   //update the bay state of the relocated stack and execute the relocation operation.
                   bay_state_simulation[stack_chosen_temp][0] = bay_state_simulation[stack_chosen_temp][0] - 1;
                   if(position[relocated_index][3] < bay_state_simulation[stack_chosen_temp][1] )
                       bay_state_simulation[stack_chosen_temp][1] = position[relocated_index][3];
                   position[relocated_index][1] = stack_chosen_temp;
                   position[relocated_index][2] = bay_state_simulation[stack_chosen_temp][0];

                   //add 1 to the counter
                   rehandle_number++;
               }
            }
            //remove the target container and update the bay state of the target stack
            bay_state_simulation[target_stack_no][0] = target_tier_no + 1;
            bay_state_simulation[target_stack_no][1] = position.length;
            for(int j=0; j<position.length; j++){
                if(position[j][3] == cur_no){
                    position[j][1] = -1;
                    position[j][2] = -1;
                }
                if(bay_state_simulation[target_stack_no][1] > position[j][3] && position[j][1] == target_stack_no)
                    bay_state_simulation[target_stack_no][1] = position[j][3];
            }
        }
        return rehandle_number;
    }


}

//using a static indicator
class Evaluating_rehandling{
    int total_evaluation_value;
    int total_sequence_size;
    int evaluate_objective(int[][] input){
        int objective_value = 0;
        for(int i=0; i<input.length; i++){
            int current_container = input[i][0];
            for(int j=0; j<input.length; j++){
                if(current_container>input[j][0]) // the jth container is lighter than the ith one.
                    if(input[i][1]==input[j][1] && input[i][2]>input[j][2]){
                        //the jth and ith containers are in the same stack and the ith one is stacked below the jth one.
                        // the smaller value of input[i][2], the higher tier
                        /*
                        objective_value = objective_value + 1; //the original evaluation methods
                        */
                        objective_value = objective_value + (input[i][2]-input[j][2]);
                        /*
                        if(current_container/input[j][0]==10)
                            objective_value = objective_value + (input[i][2]-input[j][2]);
                        if(current_container/input[j][0]==100)
                            objective_value = objective_value + (input[i][2]-input[j][2])*2;
                        if(current_container/input[j][0]==1000)
                            objective_value = objective_value + (input[i][2]-input[j][2])*3;
                         */
                         
                    }
            }
        }
        return objective_value;       
    }

    void evaluate_function_simplified(int s, int t, String[] gn){
        Calculate_total_rehandle_prepare dataarray = new Calculate_total_rehandle_prepare();
        dataarray.datainput_simplified("store_value"+String.valueOf(s)+String.valueOf(t)+String.valueOf(gn.length)+".txt", s, gn);
        //for(int i=0; i<dataarray.size; i++)
            //System.out.println(i+" "+dataarray.next_value[i][1]);

        String[][] string_generate = dataarray.generate_string(s, t, gn);
        total_sequence_size = string_generate.length;
        int[] total_rehandle = new int[string_generate.length];
        for(int i=0; i<total_rehandle.length; i++)
            total_rehandle[i]=0;
        int final_total_rehandle =0;

        long begin_time8, end_time8, duration8;
        Date mydate3 = new Date();
        begin_time8 = mydate3.getTime();
        for(int i=0; i<string_generate.length; i++){
            //if(i==35){
            String[] target = string_generate[i];
            int[][] bay_state = new int[s][2]; //used to describe the state of the bay
            for(int k=0; k<s; k++){
                bay_state[k][0]=t; //initialize bay_state
                bay_state[k][1]=0;
            }
            int[][] position = new int[target.length][3]; // used to describe the positions of the containers
            String cur_nc="";
            String cur_sc= "";
            //the initial state
            for(int k=0; k<s; k++){
                cur_nc=cur_nc+ String.valueOf(t);
                cur_sc=cur_sc+String.valueOf(0);
            }
            cur_nc=cur_nc+cur_sc;
            int start_index = dataarray.size-1;
            for(int j=0; j<target.length;j++){
                int target_index=0;
                //find the target index
                for(int k=start_index; k>=0; k--){
                    if(cur_nc.equals(dataarray.current_state[k][0])){                        
                            target_index=k;
                            start_index = k;
                            break;                       
                    }
                }
                //find the position of the target weight group
                int target_k=0;
                for(int k=0; k<gn.length; k++){
                    if(gn[k].equals(target[j])){
                        target_k=k;
                        break;
                    }
                }
                cur_nc = dataarray.next_state[target_index][target_k];

                int stack_no = 0;
                for(int k=0; k<s; k++){
                    if(bay_state[k][0]==dataarray.state_number_chosen[target_index][target_k])
                        if(bay_state[k][1]==dataarray.state_weight_chosen[target_index][target_k]){
                            //if(Math.random()<0.5){
                                stack_no = k;
                                break;
                            //}
                            //if(k==s-1)
                             //   k=0;
                        }
                }
                bay_state[stack_no][0] = bay_state[stack_no][0]-1;
                if(bay_state[stack_no][0]==0)
                    bay_state[stack_no][1] = t;
                else{
                    if(target_k==gn.length-1)
                        bay_state[stack_no][1] = bay_state[stack_no][1] + 1;
                    if(target_k==gn.length-2)
                        bay_state[stack_no][1] = bay_state[stack_no][1] + 10;
                    if(target_k==gn.length-3)
                        bay_state[stack_no][1] = bay_state[stack_no][1] + 100;
                    if(target_k==gn.length-4)
                        bay_state[stack_no][1] = bay_state[stack_no][1] + 1000;
                }




                if(target_k==gn.length-1)
                    position[j][0]=1;
                if(target_k==gn.length-2)
                    position[j][0]=10;
                if(target_k==gn.length-3)
                    position[j][0]=100;
                if(target_k==gn.length-4)
                    position[j][0]=1000;
                position[j][1] = stack_no;
                position[j][2] = dataarray.state_number_chosen[target_index][target_k];
                //System.out.println("bay_state   "+ position[j][0]+"   "+ position[j][1] + "   "+ position[j][2]);
                //System.out.println(target[j]+"  "+position[j][0]);
                //total_rehandle[i] = total_rehandle[i] + position[j][0]*position[j][1];
                //System.out.print("next value:   "+cur_nc + "  ");
                //for(int h=0; h<s; h++){
                  //  System.out.print(cur_sc[h]+" ");
              //  }
              //  System.out.println(dataarray.next_value[target_index][target_k]);
            }
            total_rehandle[i] = evaluate_objective(position);
            System.out.print(i+"  ");
            for(int h=0; h<target.length; h++){
                System.out.print(target[h]+" ");
            }
            System.out.println(total_rehandle[i]);
            //System.out.println();
            final_total_rehandle = final_total_rehandle+total_rehandle[i];
        }
        Date mydate4 = new Date();
        end_time8 = mydate4.getTime();
        duration8 = end_time8 - begin_time8;
        System.out.println("total rehandle times:   "+final_total_rehandle);
        total_evaluation_value = final_total_rehandle;
        myproperties p = new myproperties();
        try{
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "total_rehandel_times"+String.valueOf(s)+String.valueOf(t)+String.valueOf(gn.length)+".txt",false);
            PrintWriter write = new PrintWriter(fw);
            write.println(s+"\t"+t+"\t"+string_generate.length+"\t"+final_total_rehandle+"\t"+duration8 +"\t");
             fw.close();
            write.close();
            }catch(IOException f){
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("error", f.toString());
            }
    }

    void evaluate_function_new(int s, int t, String[] gn){
        Calculate_total_rehandle_prepare dataarray = new Calculate_total_rehandle_prepare();
        dataarray.datainput("store_value"+String.valueOf(s)+String.valueOf(t)+".txt", s, gn);
        //for(int i=0; i<dataarray.size; i++)
            //System.out.println(i+" "+dataarray.next_value[i][1]);

        String[][] string_generate = dataarray.generate_string(s, t, gn);
        int[] total_rehandle = new int[string_generate.length];
        for(int i=0; i<total_rehandle.length; i++)
            total_rehandle[i]=0;
        int final_total_rehandle =0;

        for(int i=0; i<string_generate.length; i++){
            String[] target = string_generate[i];
            int[][] bay_state = new int[s][2]; //used to describe the state of the state
            for(int k=0; k<s; k++){
                bay_state[k][0]=t; //initialize bay_state
                bay_state[k][1]=0;
            }
            int[][] position = new int[target.length][3]; // used to describe the positions of the containers
            String cur_nc="";
            String[] cur_sc= new String[s];
            for(int k=0; k<s; k++)
                cur_sc[k]="";
            //the initial state
            for(int k=0; k<s; k++){
                cur_nc=cur_nc+ String.valueOf(t);
                cur_sc[k]=String.valueOf(0);
            }
            for(int j=0; j<target.length;j++){
                int target_index=0;
                //find the target index
                for(int k=0; k<dataarray.size; k++){
                    if(cur_nc.equals(dataarray.current_state[k][0])){
                        boolean find = true;
                        for(int h=0; h<s; h++){
                            if(!cur_sc[h].equals(dataarray.current_state[k][h+1]))
                                find = false;
                        }
                        if(find){
                            target_index=k;
                            break;
                        }
                    }
                }
                //find the position of the target weight group
                int target_k=0;
                for(int k=0; k<gn.length; k++){
                    if(gn[k].equals(target[j])){
                        target_k=k;
                        break;
                    }
                }
                cur_nc = dataarray.next_state[target_index][(s+1)*target_k+0];
                for(int h=0; h<s; h++)
                    cur_sc[h] = dataarray.next_state[target_index][(s+1)*target_k+h+1];

                int stack_no = 0;
                for(int k=0; k<s; k++){
                    if(bay_state[k][0]==dataarray.state_number_chosen[target_index][target_k])
                        if(bay_state[k][1]==dataarray.state_weight_chosen[target_index][target_k]){
                            //if(Math.random()<0.5){
                                stack_no = k;
                                break;
                            //}
                            //if(k==s-1)
                             //   k=0;
                        }
                }
                bay_state[stack_no][0] = bay_state[stack_no][0]-1;
                if(bay_state[stack_no][0]==0)
                    bay_state[stack_no][1] = t;
                else{
                    if(target_k==gn.length-1)
                        bay_state[stack_no][1] = bay_state[stack_no][1] + 1;
                    if(target_k==gn.length-2)
                        bay_state[stack_no][1] = bay_state[stack_no][1] + 10;
                    if(target_k==gn.length-3)
                        bay_state[stack_no][1] = bay_state[stack_no][1] + 100;
                    if(target_k==gn.length-4)
                        bay_state[stack_no][1] = bay_state[stack_no][1] + 1000;
                }



                
                if(target_k==gn.length-1)
                    position[j][0]=1;
                if(target_k==gn.length-2)
                    position[j][0]=10;
                if(target_k==gn.length-3)
                    position[j][0]=100;
                if(target_k==gn.length-4)
                    position[j][0]=1000;
                position[j][1] = stack_no;
                position[j][2] = dataarray.tier_no[target_index][target_k];
                //System.out.println("bay_state   "+ position[j][0]+"   "+ position[j][1] + "   "+ position[j][2]);
                //System.out.println(target[j]+"  "+position[j][0]);
                //total_rehandle[i] = total_rehandle[i] + position[j][0]*position[j][1];
                //System.out.print("next value:   "+cur_nc + "  ");
                //for(int h=0; h<s; h++){
                  //  System.out.print(cur_sc[h]+" ");
              //  }
              //  System.out.println(dataarray.next_value[target_index][target_k]);
            }
            total_rehandle[i] = evaluate_objective(position);
            System.out.print(i+"  ");
            for(int h=0; h<target.length; h++){
                System.out.print(target[h]+" ");
            }
            System.out.println(total_rehandle[i]);
            //System.out.println();
            final_total_rehandle = final_total_rehandle+total_rehandle[i];
        }
        System.out.println("total rehandle times:   "+final_total_rehandle);
        total_evaluation_value = final_total_rehandle;
        myproperties p = new myproperties();
        try{
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "total_rehandel_times"+String.valueOf(s)+String.valueOf(t)+".txt",false);
            PrintWriter write = new PrintWriter(fw);
            write.println(s+"\t"+t+"\t"+string_generate.length+"\t"+final_total_rehandle+"\t");
             fw.close();
            write.close();
            }catch(IOException f){
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("error", f.toString());
            }
    }

    void evaluate_function(int s, int t, String[] gn){
        Calculate_total_rehandle_prepare dataarray = new Calculate_total_rehandle_prepare();
        dataarray.datainput("store_value"+String.valueOf(s)+String.valueOf(t)+".txt", s, gn);
        //for(int i=0; i<dataarray.size; i++)
            //System.out.println(i+" "+dataarray.next_value[i][1]);

        String[][] string_generate = dataarray.generate_string(s, t, gn);        
        int[] total_rehandle = new int[string_generate.length];
        for(int i=0; i<total_rehandle.length; i++)
            total_rehandle[i]=0;
        int final_total_rehandle =0;
         
        for(int i=0; i<string_generate.length; i++){
            String[] target = string_generate[i];
            int[][] position = new int[target.length][2];
            String cur_nc="";
            String[] cur_sc= new String[s];
            for(int k=0; k<s; k++)
                cur_sc[k]="";
            //the initial state
            for(int k=0; k<s; k++){
                cur_nc=cur_nc+ String.valueOf(t);
                cur_sc[k]=String.valueOf(0);
            }
            for(int j=0; j<target.length;j++){
                int target_index=0;
                //find the target index
                for(int k=0; k<dataarray.size; k++){
                    if(cur_nc.equals(dataarray.current_state[k][0])){
                        boolean find = true;
                        for(int h=0; h<s; h++){
                            if(!cur_sc[h].equals(dataarray.current_state[k][h+1]))
                                find = false;
                        }
                        if(find){
                            target_index=k;
                            break;
                        }
                    }
                }
                //find the position of the target weight group
                int target_k=0;               
                for(int k=0; k<gn.length; k++){
                    if(gn[k].equals(target[j])){
                        target_k=k;
                        break;
                    }
                }
                cur_nc = dataarray.next_state[target_index][(s+1)*target_k+0];
                for(int h=0; h<s; h++)
                    cur_sc[h] = dataarray.next_state[target_index][(s+1)*target_k+h+1];
                position[j][0] = dataarray.tier_no[target_index][target_k];
                if(target_k==gn.length-1)
                    position[j][1]=1;
                if(target_k==gn.length-2)
                    position[j][1]=10;
                if(target_k==gn.length-3)
                    position[j][1]=100;
                if(target_k==gn.length-4)
                    position[j][1]=1000;

                //System.out.println(target[j]+"  "+position[j][0]);
                total_rehandle[i] = total_rehandle[i] + position[j][0]*position[j][1];
                //System.out.print("next value:   "+cur_nc + "  ");
                //for(int h=0; h<s; h++){
                  //  System.out.print(cur_sc[h]+" ");
              //  }
              //  System.out.println(dataarray.next_value[target_index][target_k]);
            }
            System.out.print(i+"  ");
            for(int h=0; h<target.length; h++){
                System.out.print(target[h]+" ");
            }
            System.out.println(total_rehandle[i]);
            //System.out.println();
            final_total_rehandle = final_total_rehandle+total_rehandle[i];
        }
        System.out.println("total rehandle times:   "+final_total_rehandle);
        total_evaluation_value = final_total_rehandle;
        myproperties p = new myproperties();
        try{
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "total_rehandel_times"+String.valueOf(s)+String.valueOf(t)+".txt",false);
            PrintWriter write = new PrintWriter(fw);
            write.println(s+"\t"+t+"\t"+string_generate.length+"\t"+final_total_rehandle+"\t");
             fw.close();
            write.close();
            }catch(IOException f){
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("error", f.toString());
            }
    }
}

class Calculate_total_rehandle{
    void calculate_total_rehandle(int s, int t, String[] gn){
        Calculate_total_rehandle_prepare dataarray = new Calculate_total_rehandle_prepare();
        dataarray.datainput("store_value"+String.valueOf(s)+String.valueOf(t)+".txt", s, gn);
        //for(int i=0; i<dataarray.size; i++)
            //System.out.println(i+" "+dataarray.next_value[i][1]);

        String[][] string_generate = dataarray.generate_string(s, t, gn);
        double[] total_rehandle = new double[string_generate.length];
        for(int i=0; i<total_rehandle.length; i++)
            total_rehandle[i]=0.0;
        double final_total_rehandle =0.0;
        for(int i=0; i<string_generate.length; i++){
            String[] target = string_generate[i];
            String cur_nc="";
            String[] cur_sc= new String[s];
            for(int k=0; k<s; k++)
                cur_sc[k]="";
            //the initial state
            for(int k=0; k<s; k++){
                cur_nc=cur_nc+ String.valueOf(t);
                cur_sc[k]=String.valueOf(0);
            }
            for(int j=0; j<target.length;j++){
                int target_index=0;
                //find the target index
                for(int k=0; k<dataarray.size; k++){
                    if(cur_nc.equals(dataarray.current_state[k][0])){
                        boolean find = true;
                        for(int h=0; h<s; h++){
                            if(!cur_sc[h].equals(dataarray.current_state[k][h+1]))
                                find = false;
                        }
                        if(find){
                            target_index=k;
                            break;
                        }
                    }
                }
                //find the position of the target weight group
                int target_k=0;
                for(int k=0; k<gn.length; k++){
                    if(gn[k].equals(target[j])){
                        target_k=k;
                        break;
                    }
                }
                cur_nc = dataarray.next_state[target_index][(s+1)*target_k+0];
                for(int h=0; h<s; h++)
                    cur_sc[h] = dataarray.next_state[target_index][(s+1)*target_k+h+1];
                total_rehandle[i] = total_rehandle[i] + dataarray.next_value[target_index][target_k];
                //System.out.print("next value:   "+cur_nc + "  ");
                //for(int h=0; h<s; h++){
                  //  System.out.print(cur_sc[h]+" ");
              //  }
              //  System.out.println(dataarray.next_value[target_index][target_k]);
            }
            System.out.print(i+"  ");
            for(int h=0; h<target.length; h++){
                System.out.print(target[h]+" ");
            }
            System.out.println(total_rehandle[i]);
            //System.out.println();
            final_total_rehandle = final_total_rehandle+total_rehandle[i];
        }
        System.out.println("total rehandle times:   "+final_total_rehandle);

        myproperties p = new myproperties();
        try{
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "total_rehandel_times"+String.valueOf(s)+String.valueOf(t)+".txt",false);
            PrintWriter write = new PrintWriter(fw);
            write.println(s+"\t"+t+"\t"+string_generate.length+"\t"+final_total_rehandle+"\t");
             fw.close();
            write.close();
            }catch(IOException f){
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("error", f.toString());
            }
    }
}

class Calculate_total_rehandle_prepare{
    String[][] current_state;
    double[] current_value;
    String[][] next_state;
    String[][] next_weight_group;
    double[][] next_value;
    int[][] tier_no;
    int[][] state_number_chosen;
    int[][] state_weight_chosen;
    int size;
    void datainput(String filename, int s, String[] gn){
       myproperties p = new myproperties();
       String path = p.getproperties("path");

       Input input = new Input();
       String[][] dataarray = input.readdata(path+filename);

       size = dataarray.length;
       current_state = new String[dataarray.length][1+s];
       current_value = new double[dataarray.length];
       next_state = new String[dataarray.length][(1+s)*gn.length];
       next_weight_group = new String[dataarray.length][gn.length];
       next_value = new double[dataarray.length][gn.length];
       tier_no = new int[dataarray.length][gn.length];
       state_number_chosen = new int[dataarray.length][gn.length];
       state_weight_chosen = new int[dataarray.length][gn.length];


       for(int i=0; i<dataarray.length; i++){
           current_state[i][0] = dataarray[i][0];
           for(int j=0; j<s; j++)
            current_state[i][j+1] = dataarray[i][j+1];
           current_value[i] = Double.valueOf(dataarray[i][1+s]);
           for(int k=0; k<gn.length; k++){
              next_weight_group[i][k] =  dataarray[i][(1+1+s+1+1+2)*k+(2+s)];
              next_state[i][(s+1)*k+0] = dataarray[i][(1+1+s+1+1+2)*k+(3+s)];
              for(int j=0; j<s; j++)
                next_state[i][(s+1)*k+j+1] = dataarray[i][(1+1+s+1+1+2)*k+(4+s)+j];
              next_value[i][k] = Double.valueOf(dataarray[i][(1+1+s+1+1+2)*k+(5+s)+s-1]);
              tier_no[i][k] = Integer.valueOf(dataarray[i][(1+1+s+1+1+2)*k+(6+s)+s-1]);
              state_number_chosen[i][k] = Integer.valueOf(dataarray[i][(1+1+s+1+1+2)*k+(7+s)+s-1]);
              state_weight_chosen[i][k] = Integer.valueOf(dataarray[i][(1+1+s+1+1+2)*k+(8+s)+s-1]);
           }
       }
    }

    void datainput_simplified(String filename, int s, String[] gn){
       myproperties p = new myproperties();
       String path = p.getproperties("path");

       Input input = new Input();
       String[][] dataarray = input.readdata_new(path+filename);

       size = dataarray.length;
       current_state = new String[dataarray.length][1];
       next_state = new String[dataarray.length][gn.length];
       state_number_chosen = new int[dataarray.length][gn.length];
       state_weight_chosen = new int[dataarray.length][gn.length];


       for(int i=0; i<dataarray.length; i++){
           current_state[i][0] = dataarray[i][0];
           for(int k=0; k<gn.length; k++){
              next_state[i][k] = dataarray[i][3*k+1];
              state_number_chosen[i][k] = Integer.valueOf(dataarray[i][3*k+2]);
              state_weight_chosen[i][k] = Integer.valueOf(dataarray[i][3*k+3]);
           }
       }
    }



    String[][] generate_string(int s, int t, String[] gn){
        int accumulation=1;
        for(int i=0; i<s*t; i++){
            accumulation = accumulation * gn.length;
            if(accumulation>2000000){
                accumulation = 2000000;
                break;
            }
        }

        String[][] permutation = new String[accumulation][s*t];
        int index=0;

	if(s*t==28)
            for(int i=0; i<gn.length; i++){
                for(int i1=0; i1<gn.length; i1++){
                    for(int i2=0; i2<gn.length; i2++){
                        for(int i3=0; i3<gn.length; i3++){
                            for(int i4=0; i4<gn.length; i4++){
                                for(int i5=0; i5<gn.length; i5++){
                                    for(int i6=0; i6<gn.length; i6++){
                                        for(int i7=0; i7<gn.length; i7++){
                                            for(int i8=0; i8<gn.length; i8++){
                                                for(int i9=0; i9<gn.length; i9++){
                                                    for(int j=0; j<gn.length; j++){
                                                        for(int j1=0; j1<gn.length; j1++){
                                                            for(int j2=0; j2<gn.length; j2++){
                                                                for(int j3=0; j3<gn.length; j3++){
                                                                    for(int j4=0; j4<gn.length; j4++){
                                                                        for(int j5=0; j5<gn.length; j5++){
                                                                            for(int j6=0; j6<gn.length; j6++){
                                                                                for(int j7=0; j7<gn.length; j7++){
                                                                                    for(int j8=0; j8<gn.length; j8++){
                                                                                        for(int j9=0; j9<gn.length; j9++){
                                                                                            for(int k=0; k<gn.length; k++){
                                                                                                for(int k1=0; k1<gn.length; k1++){
                                                                                                    for(int k2=0; k2<gn.length; k2++){
                                                                                                        for(int k3=0; k3<gn.length; k3++){
																for(int k4=0; k4<gn.length; k4++){
																	for(int k5=0; k5<gn.length; k5++){
																		for(int k6=0; k6<gn.length; k6++){
																			for(int k7=0; k7<gn.length; k7++){
                                                                                                        if(index<accumulation ){
																permutation[index][0] = gn[i];
                                                                                                            permutation[index][1] = gn[i1];
                                                                                                            permutation[index][2] = gn[i2];
                                                                                                            permutation[index][3] = gn[i3];
                                                                                                            permutation[index][4] = gn[i4];
                                                                                                            permutation[index][5] = gn[i5];
                                                                                                            permutation[index][6] = gn[i6];
                                                                                                            permutation[index][7] = gn[i7];
                                                                                                            permutation[index][8] = gn[i8];
                                                                                                            permutation[index][9] = gn[i9];
                                                                                                            permutation[index][10] = gn[j];
                                                                                                            permutation[index][11] = gn[j1];
                                                                                                            permutation[index][12] = gn[j2];
                                                                                                            permutation[index][13] = gn[j3];
                                                                                                            permutation[index][14] = gn[j4];
                                                                                                            permutation[index][15] = gn[j5];
                                                                                                            permutation[index][16] = gn[j6];
                                                                                                            permutation[index][17] = gn[j7];
                                                                                                            permutation[index][18] = gn[j8];
                                                                                                            permutation[index][19] = gn[j9];
																permutation[index][20] = gn[k];
                                                                                                            permutation[index][21] = gn[k1];
                                                                                                            permutation[index][22] = gn[k2];
                                                                                                            permutation[index][23] = gn[k3];
																permutation[index][24] = gn[k4];
                                                                                                            permutation[index][25] = gn[k5];
                                                                                                            permutation[index][26] = gn[k6];
                                                                                                            permutation[index][27] = gn[k7];

																index++;
                                                                                                        }
															else
																break;										
															}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}

	if(s*t==24)
            for(int i=0; i<gn.length; i++){
                for(int i1=0; i1<gn.length; i1++){
                    for(int i2=0; i2<gn.length; i2++){
                        for(int i3=0; i3<gn.length; i3++){
                            for(int i4=0; i4<gn.length; i4++){
                                for(int i5=0; i5<gn.length; i5++){
                                    for(int i6=0; i6<gn.length; i6++){
                                        for(int i7=0; i7<gn.length; i7++){
                                            for(int i8=0; i8<gn.length; i8++){
                                                for(int i9=0; i9<gn.length; i9++){
                                                    for(int j=0; j<gn.length; j++){
                                                        for(int j1=0; j1<gn.length; j1++){
                                                            for(int j2=0; j2<gn.length; j2++){
                                                                for(int j3=0; j3<gn.length; j3++){
                                                                    for(int j4=0; j4<gn.length; j4++){
                                                                        for(int j5=0; j5<gn.length; j5++){
                                                                            for(int j6=0; j6<gn.length; j6++){
                                                                                for(int j7=0; j7<gn.length; j7++){
                                                                                    for(int j8=0; j8<gn.length; j8++){
                                                                                        for(int j9=0; j9<gn.length; j9++){
                                                                                            for(int k=0; k<gn.length; k++){
                                                                                                for(int k1=0; k1<gn.length; k1++){
                                                                                                    for(int k2=0; k2<gn.length; k2++){
                                                                                                        for(int k3=0; k3<gn.length; k3++){
                                                                                                        if(index<accumulation ){
																permutation[index][0] = gn[i];
                                                                                                            permutation[index][1] = gn[i1];
                                                                                                            permutation[index][2] = gn[i2];
                                                                                                            permutation[index][3] = gn[i3];
                                                                                                            permutation[index][4] = gn[i4];
                                                                                                            permutation[index][5] = gn[i5];
                                                                                                            permutation[index][6] = gn[i6];
                                                                                                            permutation[index][7] = gn[i7];
                                                                                                            permutation[index][8] = gn[i8];
                                                                                                            permutation[index][9] = gn[i9];
                                                                                                            permutation[index][10] = gn[j];
                                                                                                            permutation[index][11] = gn[j1];
                                                                                                            permutation[index][12] = gn[j2];
                                                                                                            permutation[index][13] = gn[j3];
                                                                                                            permutation[index][14] = gn[j4];
                                                                                                            permutation[index][15] = gn[j5];
                                                                                                            permutation[index][16] = gn[j6];
                                                                                                            permutation[index][17] = gn[j7];
                                                                                                            permutation[index][18] = gn[j8];
                                                                                                            permutation[index][19] = gn[j9];
																permutation[index][20] = gn[k];
                                                                                                            permutation[index][21] = gn[k1];
                                                                                                            permutation[index][22] = gn[k2];
                                                                                                            permutation[index][23] = gn[k3];
																index++;
                                                                                                        }
															else
																break;										
															}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}

if(s*t==21)
            for(int i=0; i<gn.length; i++){
                for(int i1=0; i1<gn.length; i1++){
                    for(int i2=0; i2<gn.length; i2++){
                        for(int i3=0; i3<gn.length; i3++){
                            for(int i4=0; i4<gn.length; i4++){
                                for(int i5=0; i5<gn.length; i5++){
                                    for(int i6=0; i6<gn.length; i6++){
                                        for(int i7=0; i7<gn.length; i7++){
                                            for(int i8=0; i8<gn.length; i8++){
                                                for(int i9=0; i9<gn.length; i9++){
                                                    for(int j=0; j<gn.length; j++){
                                                        for(int j1=0; j1<gn.length; j1++){
                                                            for(int j2=0; j2<gn.length; j2++){
                                                                for(int j3=0; j3<gn.length; j3++){
                                                                    for(int j4=0; j4<gn.length; j4++){
                                                                        for(int j5=0; j5<gn.length; j5++){
                                                                            for(int j6=0; j6<gn.length; j6++){
                                                                                for(int j7=0; j7<gn.length; j7++){
                                                                                    for(int j8=0; j8<gn.length; j8++){
                                                                                        for(int j9=0; j9<gn.length; j9++){
                                                                                            for(int k=0; k<gn.length; k++){
                                                                                                        if(index<accumulation ){
																permutation[index][0] = gn[i];
                                                                                                            permutation[index][1] = gn[i1];
                                                                                                            permutation[index][2] = gn[i2];
                                                                                                            permutation[index][3] = gn[i3];
                                                                                                            permutation[index][4] = gn[i4];
                                                                                                            permutation[index][5] = gn[i5];
                                                                                                            permutation[index][6] = gn[i6];
                                                                                                            permutation[index][7] = gn[i7];
                                                                                                            permutation[index][8] = gn[i8];
                                                                                                            permutation[index][9] = gn[i9];
                                                                                                            permutation[index][10] = gn[j];
                                                                                                            permutation[index][11] = gn[j1];
                                                                                                            permutation[index][12] = gn[j2];
                                                                                                            permutation[index][13] = gn[j3];
                                                                                                            permutation[index][14] = gn[j4];
                                                                                                            permutation[index][15] = gn[j5];
                                                                                                            permutation[index][16] = gn[j6];
                                                                                                            permutation[index][17] = gn[j7];
                                                                                                            permutation[index][18] = gn[j8];
                                                                                                            permutation[index][19] = gn[j9];
																permutation[index][20] = gn[k];
																index++;
                                                                                                        }
															else
																break;										
															}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}

if(s*t==20)
            for(int i=0; i<gn.length; i++){
                for(int i1=0; i1<gn.length; i1++){
                    for(int i2=0; i2<gn.length; i2++){
                        for(int i3=0; i3<gn.length; i3++){
                            for(int i4=0; i4<gn.length; i4++){
                                for(int i5=0; i5<gn.length; i5++){
                                    for(int i6=0; i6<gn.length; i6++){
                                        for(int i7=0; i7<gn.length; i7++){
                                            for(int i8=0; i8<gn.length; i8++){
                                                for(int i9=0; i9<gn.length; i9++){
                                                    for(int j=0; j<gn.length; j++){
                                                        for(int j1=0; j1<gn.length; j1++){
                                                            for(int j2=0; j2<gn.length; j2++){
                                                                for(int j3=0; j3<gn.length; j3++){
                                                                    for(int j4=0; j4<gn.length; j4++){
                                                                        for(int j5=0; j5<gn.length; j5++){
                                                                            for(int j6=0; j6<gn.length; j6++){
                                                                                for(int j7=0; j7<gn.length; j7++){
                                                                                    for(int j8=0; j8<gn.length; j8++){
                                                                                        for(int j9=0; j9<gn.length; j9++){
                                                                                                        if(index<accumulation ){
																permutation[index][0] = gn[i];
                                                                                                            permutation[index][1] = gn[i1];
                                                                                                            permutation[index][2] = gn[i2];
                                                                                                            permutation[index][3] = gn[i3];
                                                                                                            permutation[index][4] = gn[i4];
                                                                                                            permutation[index][5] = gn[i5];
                                                                                                            permutation[index][6] = gn[i6];
                                                                                                            permutation[index][7] = gn[i7];
                                                                                                            permutation[index][8] = gn[i8];
                                                                                                            permutation[index][9] = gn[i9];
                                                                                                            permutation[index][10] = gn[j];
                                                                                                            permutation[index][11] = gn[j1];
                                                                                                            permutation[index][12] = gn[j2];
                                                                                                            permutation[index][13] = gn[j3];
                                                                                                            permutation[index][14] = gn[j4];
                                                                                                            permutation[index][15] = gn[j5];
                                                                                                            permutation[index][16] = gn[j6];
                                                                                                            permutation[index][17] = gn[j7];
                                                                                                            permutation[index][18] = gn[j8];
                                                                                                            permutation[index][19] = gn[j9];
																index++;
                                                                                                        }
															else
																break;										
															}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}

if(s*t==18)
            for(int i=0; i<gn.length; i++){
                for(int i1=0; i1<gn.length; i1++){
                    for(int i2=0; i2<gn.length; i2++){
                        for(int i3=0; i3<gn.length; i3++){
                            for(int i4=0; i4<gn.length; i4++){
                                for(int i5=0; i5<gn.length; i5++){
                                    for(int i6=0; i6<gn.length; i6++){
                                        for(int i7=0; i7<gn.length; i7++){
                                            for(int i8=0; i8<gn.length; i8++){
                                                for(int i9=0; i9<gn.length; i9++){
                                                    for(int j=0; j<gn.length; j++){
                                                        for(int j1=0; j1<gn.length; j1++){
                                                            for(int j2=0; j2<gn.length; j2++){
                                                                for(int j3=0; j3<gn.length; j3++){
                                                                    for(int j4=0; j4<gn.length; j4++){
                                                                        for(int j5=0; j5<gn.length; j5++){
                                                                            for(int j6=0; j6<gn.length; j6++){
                                                                                for(int j7=0; j7<gn.length; j7++){
                                                                                                        if(index<accumulation ){
																permutation[index][0] = gn[i];
                                                                                                            permutation[index][1] = gn[i1];
                                                                                                            permutation[index][2] = gn[i2];
                                                                                                            permutation[index][3] = gn[i3];
                                                                                                            permutation[index][4] = gn[i4];
                                                                                                            permutation[index][5] = gn[i5];
                                                                                                            permutation[index][6] = gn[i6];
                                                                                                            permutation[index][7] = gn[i7];
                                                                                                            permutation[index][8] = gn[i8];
                                                                                                            permutation[index][9] = gn[i9];
                                                                                                            permutation[index][10] = gn[j];
                                                                                                            permutation[index][11] = gn[j1];
                                                                                                            permutation[index][12] = gn[j2];
                                                                                                            permutation[index][13] = gn[j3];
                                                                                                            permutation[index][14] = gn[j4];
                                                                                                            permutation[index][15] = gn[j5];
                                                                                                            permutation[index][16] = gn[j6];
                                                                                                            permutation[index][17] = gn[j7];
																index++;
                                                                                                        }
															else
																break;										
															}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}


if(s*t==16)
            for(int i=0; i<gn.length; i++){
                for(int i1=0; i1<gn.length; i1++){
                    for(int i2=0; i2<gn.length; i2++){
                        for(int i3=0; i3<gn.length; i3++){
                            for(int i4=0; i4<gn.length; i4++){
                                for(int i5=0; i5<gn.length; i5++){
                                    for(int i6=0; i6<gn.length; i6++){
                                        for(int i7=0; i7<gn.length; i7++){
                                            for(int i8=0; i8<gn.length; i8++){
                                                for(int i9=0; i9<gn.length; i9++){
                                                    for(int j=0; j<gn.length; j++){
                                                        for(int j1=0; j1<gn.length; j1++){
                                                            for(int j2=0; j2<gn.length; j2++){
                                                                for(int j3=0; j3<gn.length; j3++){
                                                                    for(int j4=0; j4<gn.length; j4++){
                                                                        for(int j5=0; j5<gn.length; j5++){
                                                                                                        if(index<accumulation ){
																permutation[index][0] = gn[i];
                                                                                                            permutation[index][1] = gn[i1];
                                                                                                            permutation[index][2] = gn[i2];
                                                                                                            permutation[index][3] = gn[i3];
                                                                                                            permutation[index][4] = gn[i4];
                                                                                                            permutation[index][5] = gn[i5];
                                                                                                            permutation[index][6] = gn[i6];
                                                                                                            permutation[index][7] = gn[i7];
                                                                                                            permutation[index][8] = gn[i8];
                                                                                                            permutation[index][9] = gn[i9];
                                                                                                            permutation[index][10] = gn[j];
                                                                                                            permutation[index][11] = gn[j1];
                                                                                                            permutation[index][12] = gn[j2];
                                                                                                            permutation[index][13] = gn[j3];
                                                                                                            permutation[index][14] = gn[j4];
                                                                                                            permutation[index][15] = gn[j5];
																index++;
                                                                                                        }
															else
																break;										
															}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}


if(s*t==15)
            for(int i=0; i<gn.length; i++){
                for(int i1=0; i1<gn.length; i1++){
                    for(int i2=0; i2<gn.length; i2++){
                        for(int i3=0; i3<gn.length; i3++){
                            for(int i4=0; i4<gn.length; i4++){
                                for(int i5=0; i5<gn.length; i5++){
                                    for(int i6=0; i6<gn.length; i6++){
                                        for(int i7=0; i7<gn.length; i7++){
                                            for(int i8=0; i8<gn.length; i8++){
                                                for(int i9=0; i9<gn.length; i9++){
                                                    for(int j=0; j<gn.length; j++){
                                                        for(int j1=0; j1<gn.length; j1++){
                                                            for(int j2=0; j2<gn.length; j2++){
                                                                for(int j3=0; j3<gn.length; j3++){
                                                                    for(int j4=0; j4<gn.length; j4++){
                                                                                                        if(index<accumulation ){
																permutation[index][0] = gn[i];
                                                                                                            permutation[index][1] = gn[i1];
                                                                                                            permutation[index][2] = gn[i2];
                                                                                                            permutation[index][3] = gn[i3];
                                                                                                            permutation[index][4] = gn[i4];
                                                                                                            permutation[index][5] = gn[i5];
                                                                                                            permutation[index][6] = gn[i6];
                                                                                                            permutation[index][7] = gn[i7];
                                                                                                            permutation[index][8] = gn[i8];
                                                                                                            permutation[index][9] = gn[i9];
                                                                                                            permutation[index][10] = gn[j];
                                                                                                            permutation[index][11] = gn[j1];
                                                                                                            permutation[index][12] = gn[j2];
                                                                                                            permutation[index][13] = gn[j3];
                                                                                                            permutation[index][14] = gn[j4];
																index++;
                                                                                                        }
															else
																break;										
															}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}

if(s*t==12)
            for(int i=0; i<gn.length; i++){
                for(int i1=0; i1<gn.length; i1++){
                    for(int i2=0; i2<gn.length; i2++){
                        for(int i3=0; i3<gn.length; i3++){
                            for(int i4=0; i4<gn.length; i4++){
                                for(int i5=0; i5<gn.length; i5++){
                                    for(int i6=0; i6<gn.length; i6++){
                                        for(int i7=0; i7<gn.length; i7++){
                                            for(int i8=0; i8<gn.length; i8++){
                                                for(int i9=0; i9<gn.length; i9++){
                                                    for(int j=0; j<gn.length; j++){
                                                        for(int j1=0; j1<gn.length; j1++){
                                                                                                        if(index<accumulation ){
																permutation[index][0] = gn[i];
                                                                                                            permutation[index][1] = gn[i1];
                                                                                                            permutation[index][2] = gn[i2];
                                                                                                            permutation[index][3] = gn[i3];
                                                                                                            permutation[index][4] = gn[i4];
                                                                                                            permutation[index][5] = gn[i5];
                                                                                                            permutation[index][6] = gn[i6];
                                                                                                            permutation[index][7] = gn[i7];
                                                                                                            permutation[index][8] = gn[i8];
                                                                                                            permutation[index][9] = gn[i9];
                                                                                                            permutation[index][10] = gn[j];
                                                                                                            permutation[index][11] = gn[j1];
																index++;
                                                                                                        }
															else
																break;										
															}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}

if(s*t==9)
            for(int i=0; i<gn.length; i++){
                for(int i1=0; i1<gn.length; i1++){
                    for(int i2=0; i2<gn.length; i2++){
                        for(int i3=0; i3<gn.length; i3++){
                            for(int i4=0; i4<gn.length; i4++){
                                for(int i5=0; i5<gn.length; i5++){
                                    for(int i6=0; i6<gn.length; i6++){
                                        for(int i7=0; i7<gn.length; i7++){
                                            for(int i8=0; i8<gn.length; i8++){
                                                                                                        if(index<accumulation ){
																permutation[index][0] = gn[i];
                                                                                                            permutation[index][1] = gn[i1];
                                                                                                            permutation[index][2] = gn[i2];
                                                                                                            permutation[index][3] = gn[i3];
                                                                                                            permutation[index][4] = gn[i4];
                                                                                                            permutation[index][5] = gn[i5];
                                                                                                            permutation[index][6] = gn[i6];
                                                                                                            permutation[index][7] = gn[i7];
                                                                                                            permutation[index][8] = gn[i8];
																index++;
                                                                                                        }
															else
																break;										
															}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}
if(index>=accumulation)
break;
}



	

        if(s*t==4)
            for(int i=0; i<gn.length; i++)
                for(int i1=0; i1<gn.length; i1++)
                    for(int i2=0; i2<gn.length; i2++)
                        for(int i3=0; i3<gn.length; i3++){
                                permutation[index][0] = gn[i];
                                permutation[index][1] = gn[i1];
                                permutation[index][2] = gn[i2];
                                permutation[index][3] = gn[i3];
                                index++;
                                }


        return permutation;
    }
}


//.txt文件输入�?
class Input {
    String[][] readdata_new(String filename) {
        String[] content = null;
        int linenu = 0;
        int totalline = 0;
        String line = null;
        int column_number =0;
        //先统计文件有多少�?
        ArrayList values = new ArrayList();
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            boolean first_time = true;
            while ((line = br.readLine()) != null) {
                String[] ss = line.split("\t");
                for(int j=0; j<ss.length; j++)
                    values.add(ss[j]);
                if(first_time){
                    column_number=line.split("\t").length;
                    first_time = false;
                }
                totalline = totalline + 1;
            }
            fr.close();
            br.close();
        } catch (IOException ae) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("警告 ", "可能的原因是�?.相应的文件没有找到；2." + ae.toString());
        }

        String[][] res = new String[totalline][column_number];
        for(int i=0; i<totalline; i++)
            for(int j=0; j<column_number; j++)
                res[i][j]=String.valueOf(values.get(i*column_number+j));
        /*
        //逐行保存到数据里边去
        try {
            FileReader fr1 = new FileReader(filename);
            BufferedReader br1 = new BufferedReader(fr1);
            while ((line = br1.readLine()) != null) {
                content = line.split("\t");
                res[linenu] = content;
                linenu = linenu + 1;
            }
            fr1.close();
            br1.close();

        } catch (IOException ae) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("警告 ", "可能的原因是�?.相应的文件没有找到；2." + ae.toString());
        }
        */
        return res;
    }

    String[][] readdata(String filename) {
        String[] content = null;
        int linenu = 0;
        int totalline = 0;
        String line = null;
        //先统计文件有多少�?
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                totalline = totalline + 1;
            }
            fr.close();
            br.close();
        } catch (IOException ae) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("警告 ", "可能的原因是�?.相应的文件没有找到；2." + ae.toString());
        }

        String[][] res = new String[totalline][];
        //逐行保存到数据里边去
        try {
            FileReader fr1 = new FileReader(filename);
            BufferedReader br1 = new BufferedReader(fr1);
            while ((line = br1.readLine()) != null) {
                content = line.split("\t");
                res[linenu] = content;
                linenu = linenu + 1;
            }
            fr1.close();
            br1.close();

        } catch (IOException ae) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("警告 ", "可能的原因是�?.相应的文件没有找到；2." + ae.toString());
        }
        return res;
    }
}



public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Dynamic_programming dynamic_programming = new Dynamic_programming();        
        myproperties p = new myproperties();

        //for the case with three weight groups.
         // using dymanic programming to calculate the optimal locations.
/*
        int[] gn ={10,1};
        double[] ratio={0.5, 0.5};
        long begin_time, end_time, duration;
        try{
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "final2.txt",false);
            PrintWriter write = new PrintWriter(fw);
            for(int s=4; s<5; s++)
                for(int t=3; t<4; t++){
                   //if(s*t<=24){
                    Date mydate = new Date();
                    begin_time = mydate.getTime();
                    dynamic_programming.dynamic_programming_simplified(s, t, gn, ratio,true);
                    Date mydate2 = new Date();
                    end_time = mydate2.getTime();
                    duration = end_time - begin_time;
                    write.println(s+"\t"+t+"\t"+2+"\t"+dynamic_programming.state_size+"\t"+dynamic_programming.final_value+"\t"+duration);
                }
            fw.close();
            write.close();
            }catch(IOException f){
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("error", f.toString());
            }

 
       //using the static indicator to test the quality of the new model.
        long begin_time1, end_time1, duration1;
        try{
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "evaluation2.txt",false);
            PrintWriter write = new PrintWriter(fw);
            for(int s=4; s<5; s++)
                for(int t=3; t<4; t++){
                   //if(s*t<=24){
                String[] gn_special ={"10","1"};
                Date mydate = new Date();
                begin_time1 = mydate.getTime();
                Evaluating_rehandling evaluate = new Evaluating_rehandling();
                evaluate.evaluate_function_simplified(s,t, gn_special);
                Date mydate2 = new Date();
                end_time1 = mydate2.getTime();
                duration1 = end_time1 - begin_time1;
                write.println(s+"\t"+t+"\t"+2+"\t"+evaluate.total_sequence_size+"\t"+evaluate.total_evaluation_value+"\t"+duration1);
                }
            fw.close();
            write.close();
            }catch(IOException f){
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("error", f.toString());
            }
*/
  /*
        long begin_time5, end_time5, duration5;
        try{
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "dynamic2.txt",false);
            PrintWriter write = new PrintWriter(fw);
            for(int s=4; s<8; s++)
                for(int t=3; t<5; t++){
                   //if(s*t<=24){
                String[] gn_special ={"10","1"};
                Date mydate = new Date();
                begin_time5 = mydate.getTime();
                Evaluating_simulation evaluating_simulation = new Evaluating_simulation();
                evaluating_simulation.calculate_rehandle(s, t, gn_special);
                Date mydate2 = new Date();
                end_time5 = mydate2.getTime();
                duration5 = end_time5 - begin_time5;
                write.println(s+"\t"+t+"\t"+2+"\t"+evaluating_simulation.total_sequence_size+"\t"+evaluating_simulation.total_evaluation_value+"\t"+duration5);
                }
            fw.close();
            write.close();
            }catch(IOException f){
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("error", f.toString());
            }
*/
         //for the case with three weight groups.
         // using dymanic programming to calculate the optimal locations.
        int[] gn2 ={100,10,1};
        double[] ratio2={0.33,0.33, 0.33};
        long begin_time2, end_time2, duration2;
        try{
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "final3.txt",false);
            PrintWriter write = new PrintWriter(fw);
            for(int s=4; s<5; s++)
                for(int t=3; t<4; t++){
                   //if(s*t==24){
                    Date mydate = new Date();
                    begin_time2 = mydate.getTime();
                    dynamic_programming.dynamic_programming_simplified(s, t, gn2, ratio2,true);
                    Date mydate2 = new Date();
                    end_time2 = mydate2.getTime();
                    duration2 = end_time2 - begin_time2;
                    write.println(s+"\t"+t+"\t"+3+"\t"+dynamic_programming.state_size+"\t"+dynamic_programming.final_value+"\t"+duration2);
                }
            fw.close();
            write.close();
            }catch(IOException f){
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("error", f.toString());
            }

   /*
        //using the static indicator to test the quality of the new model.
        long begin_time3, end_time3, duration3;
        try{
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "evaluation3.txt",false);
            PrintWriter write = new PrintWriter(fw);
            for(int s=6; s<8; s++)
                for(int t=3; t<5; t++){
                   //if(s*t<=24){
                String[] gn_special ={"100","10","1"};
                Date mydate = new Date();
                begin_time3 = mydate.getTime();
                Evaluating_rehandling evaluate = new Evaluating_rehandling();
                evaluate.evaluate_function_simplified(s,t, gn_special);
                Date mydate2 = new Date();
                end_time3 = mydate2.getTime();
                duration3 = end_time3 - begin_time3;
                write.println(s+"\t"+t+"\t"+3+"\t"+evaluate.total_sequence_size+"\t"+evaluate.total_evaluation_value+"\t"+duration3);
                }
            fw.close();
            write.close();
            }catch(IOException f){
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("error", f.toString());
            }
   */

/*
        //using the dynamic indicator to test the quality of the new model.
        long begin_time4, end_time4, duration4;
        try{
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "dynamic3.txt",false);
            PrintWriter write = new PrintWriter(fw);
            for(int s=7; s<8; s++)
                for(int t=4; t<5; t++){
                   //if(s*t<=24){
                String[] gn_special ={"100","10","1"};
                Date mydate = new Date();
                begin_time4 = mydate.getTime();
                Evaluating_simulation evaluating_simulation = new Evaluating_simulation();
                evaluating_simulation.calculate_rehandle(s, t, gn_special);
                Date mydate2 = new Date();
                end_time4 = mydate2.getTime();
                duration4 = end_time4 - begin_time4;
                write.println(s+"\t"+t+"\t"+3+"\t"+evaluating_simulation.total_sequence_size+"\t"+evaluating_simulation.total_evaluation_value+"\t"+duration4);
                }
            fw.close();
            write.close();
            }catch(IOException f){
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("error", f.toString());
            }
*/

    }

}
