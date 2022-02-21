/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package locationassignment;

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
                                "/Users/jacqueline/Code/location assignment/data/config.properties"));
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
    String[][] next_state;
    String[] next_weight_group;
    double[] next_value;
    int[] tier_no;
    int[] state_number_chosen;
    String[] state_weight_chosen;

    double calculate_value(int t, String nc, String sc, String[] gn, String[] nc_pre, String[] sc_pre, double[] va_pre, double[] ratio) {
        next_state = new String[gn.length][2];
        next_weight_group = new String[gn.length];
        next_value = new double[gn.length];
        tier_no = new int[gn.length];
        state_number_chosen = new int[gn.length];
        state_weight_chosen = new String[gn.length];
        double[] objective_value = new double[gn.length];
        for (int i = 0; i < gn.length; i++)
            objective_value[i] = Double.MAX_VALUE;
        for (int i = 0; i < gn.length; i++) {
            for (int j = 0; j < nc.length(); j++) {
                if (nc.charAt(j) > '0') {
                    String nc_temp = nc;
                    String sc_temp = sc;
                    int tier_no_temp = Integer.parseInt(String.valueOf(nc.charAt(j)));
                    String state_weight_chosen_temp = String.valueOf(sc.charAt(j));
                    int rehandle_times = 0;
                    int position = gn.length; //gn.length represents "*"
                    for (int ii = 0; ii < gn.length; ii++)
                        if (sc_temp.charAt(j) == gn[ii].charAt(0))
                            position = ii;

                    //the original model
                    if (position >= i)
                        rehandle_times = 0; //the arriving container has higher weigh group
                    else
                        rehandle_times = 1;
                     /*
                    //the revised original model
                    if(position>=i)
                       rehandle_times = 0; //the arriving container has higher weigh group
                    else{
                       rehandle_times = (int) Math.ceil((double) (t-tier_no_temp)/2);
                    }
                    */
                    //adjust nc
                    String first_part = "";
                    String third_part = "";
                    String second_part = "";
                    if (j > 0)
                        first_part = nc_temp.substring(0, j - 1 - 0 + 1);
                    if (j < nc.length() - 1)
                        third_part = nc_temp.substring(j + 1, nc_temp.length() - 1 - (j + 1) + 1 + (j + 1));
                    int left_empty = Integer.parseInt(String.valueOf(nc_temp.charAt(j))) - 1;
                    second_part = String.valueOf(left_empty);
                    nc_temp = first_part + second_part + third_part;
                    //adjust sc
                    String first_part1 = "";
                    String third_part1 = "";
                    String second_part1 = "";
                    if (left_empty == 0) {
                        if (j > 0)
                            first_part1 = sc_temp.substring(0, j - 1 - 0 + 1);
                        if (j < sc.length() - 1)
                            third_part1 = sc_temp.substring(j + 1, sc_temp.length() - 1 - (j + 1) + 1 + (j + 1));
                        second_part1 = "0";
                        sc_temp = first_part1 + second_part1 + third_part1;
                    } else {
                        if (rehandle_times == 0) {
                            if (j > 0)
                                first_part1 = sc_temp.substring(0, j - 1 - 0 + 1);
                            if (j < sc.length() - 1)
                                third_part1 = sc_temp.substring(j + 1, sc_temp.length() - 1 - (j + 1) + 1 + (j + 1));
                            second_part1 = gn[i];
                            sc_temp = first_part1 + second_part1 + third_part1;
                        }
                    }
                    //re-sequence nc and sc
                    for (int k = 0; k < nc_temp.length() - 1; k++) {
                        int first = Integer.parseInt(String.valueOf(nc_temp.charAt(k)));
                        int second = Integer.parseInt(String.valueOf(nc_temp.charAt(k + 1)));
                        String first_part2 = "";
                        String fourth_part2 = "";
                        String second_part2 = "";
                        String third_part2 = "";
                        String first_part3 = "";
                        String fourth_part3 = "";
                        String second_part3 = "";
                        String third_part3 = "";
                        if (first < second) {
                            if (k > 0)
                                first_part2 = nc_temp.substring(0, k - 1 - 0 + 1);
                            if (k < nc.length() - 2)
                                fourth_part2 = nc_temp.substring(k + 2, nc_temp.length() - 1 - (k + 2) + 1 + (k + 2));
                            second_part2 = String.valueOf(second);
                            third_part2 = String.valueOf(first);
                            nc_temp = first_part2 + second_part2 + third_part2 + fourth_part2;

                            if (k > 0)
                                first_part3 = sc_temp.substring(0, k - 1 - 0 + 1);
                            if (k < sc.length() - 2)
                                fourth_part3 = sc_temp.substring(k + 2, sc_temp.length() - 1 - (k + 2) + 1 + (k + 2));
                            second_part3 = String.valueOf(sc_temp.charAt(k + 1));
                            third_part3 = String.valueOf(sc_temp.charAt(k));
                            sc_temp = first_part3 + second_part3 + third_part3 + fourth_part3;
                        }
                    }
                    //re-sequence sc
                    boolean needed = true;
                    while (needed) {
                        needed = false;
                        for (int k = 0; k < nc_temp.length() - 1; k++) {
                            int first = Integer.parseInt(String.valueOf(nc_temp.charAt(k)));
                            int second = Integer.parseInt(String.valueOf(nc_temp.charAt(k + 1)));
                            int first1 = 0;
                            int second1 = 0;
                            for (int h = 0; h < gn.length; h++) {
                                if (gn[h].equals(String.valueOf(sc_temp.charAt(k))))
                                    first1 = h;
                                if (gn[h].equals(String.valueOf(sc_temp.charAt(k + 1))))
                                    second1 = h;
                            }


                            String first_part3 = "";
                            String fourth_part3 = "";
                            String second_part3 = "";
                            String third_part3 = "";
                            if (first == second && first1 > second1) {
                                needed = true;
                                if (k > 0)
                                    first_part3 = sc_temp.substring(0, k - 1 - 0 + 1);
                                if (k < sc.length() - 2)
                                    fourth_part3 = sc_temp.substring(k + 2, sc_temp.length() - 1 - (k + 2) + 1 + (k + 2));
                                second_part3 = String.valueOf(sc_temp.charAt(k + 1));
                                third_part3 = String.valueOf(sc_temp.charAt(k));
                                sc_temp = first_part3 + second_part3 + third_part3 + fourth_part3;
                            }
                        }
                    }

                    //match the states;
                    double following_value = 0.0;
                    for (int k = 0; k < nc_pre.length; k++) {
                        if (nc_temp.equals(nc_pre[k]) && sc_temp.equals(sc_pre[k])) {
                            following_value = va_pre[k];
                            break;
                        }
                    }

                    //calculate objective value
                    double value_temp = following_value + rehandle_times;
                    if (objective_value[i] > value_temp) {
                        objective_value[i] = value_temp;
                        next_state[i][0] = nc_temp;
                        next_state[i][1] = sc_temp;
                        next_weight_group[i] = gn[i];
                        next_value[i] = rehandle_times;
                        tier_no[i] = tier_no_temp;
                        state_number_chosen[i] = tier_no_temp;
                        state_weight_chosen[i] = state_weight_chosen_temp;
                    }
                }
            }
        }
        double total_objective_value = 0.0;
        for (int i = 0; i < objective_value.length; i++)
            total_objective_value = total_objective_value + objective_value[i] * ratio[i];
        return total_objective_value;
    }

    int dimension_size(int s, int t, int n, String[] gn, String[] result) {
        int total_number = 0;
        for (int i = 0; i < result.length; i++) {
            total_number = total_number + permutation_size(result[i], t, gn);
        }
        return total_number;
    }

    String[] solution_space(int s, int t, int n) {
        int total_number = 0;
        if (s == 2) {
            for (int i = t; i >= 0; i--)
                for (int ii = i; ii >= 0; ii--) {
                    if (i + ii == n)
                        total_number++;
                }
        }
        if (s == 3) {
            for (int i = t; i >= 0; i--)
                for (int ii = i; ii >= 0; ii--)
                    for (int j = ii; j >= 0; j--) {
                        if (i + ii + j == n)
                            total_number++;
                    }
        }
        if (s == 4) {
            for (int i = t; i >= 0; i--)
                for (int ii = i; ii >= 0; ii--)
                    for (int j = ii; j >= 0; j--)
                        for (int jj = j; jj >= 0; jj--) {
                            if (i + ii + j + jj == n)
                                total_number++;
                        }
        }
        if (s == 5) {
            for (int i = t; i >= 0; i--)
                for (int ii = i; ii >= 0; ii--)
                    for (int j = ii; j >= 0; j--)
                        for (int jj = j; jj >= 0; jj--)
                            for (int k = jj; k >= 0; k--) {
                                if (i + ii + j + jj + k == n)
                                    total_number++;
                            }
        }
        if (s == 6) {
            for (int i = t; i >= 0; i--)
                for (int ii = i; ii >= 0; ii--)
                    for (int j = ii; j >= 0; j--)
                        for (int jj = j; jj >= 0; jj--)
                            for (int k = jj; k >= 0; k--)
                                for (int kk = k; kk >= 0; kk--) {
                                    if (i + ii + j + jj + k + kk == n)
                                        total_number++;
                                }
        }
        if (s == 7) {
            for (int i = t; i >= 0; i--)
                for (int ii = i; ii >= 0; ii--)
                    for (int j = ii; j >= 0; j--)
                        for (int jj = j; jj >= 0; jj--)
                            for (int k = jj; k >= 0; k--)
                                for (int kk = k; kk >= 0; kk--)
                                    for (int h = kk; h >= 0; h--) {
                                        if (i + ii + j + jj + k + kk + h == n)
                                            total_number++;
                                    }
        }
        if (s == 8) {
            for (int i = t; i >= 0; i--)
                for (int ii = i; ii >= 0; ii--)
                    for (int j = ii; j >= 0; j--)
                        for (int jj = j; jj >= 0; jj--)
                            for (int k = jj; k >= 0; k--)
                                for (int kk = k; kk >= 0; kk--)
                                    for (int h = kk; h >= 0; h--)
                                        for (int hh = h; hh >= 0; hh--) {
                                            if (i + ii + j + jj + k + kk + h + hh == n)
                                                total_number++;
                                        }
        }


        String[] result = new String[total_number];
        int index = 0;


        if (s == 2) {
            for (int i = t; i >= 0; i--)
                for (int ii = i; ii >= 0; ii--)
                    if (i + ii == n) {
                        result[index] = String.valueOf(i) + String.valueOf(ii);
                        index++;
                    }
        }
        if (s == 3) {
            for (int i = t; i >= 0; i--)
                for (int ii = i; ii >= 0; ii--)
                    for (int j = ii; j >= 0; j--) {
                        if (i + ii + j == n) {
                            result[index] = String.valueOf(i) + String.valueOf(ii) + String.valueOf(j);
                            index++;
                        }
                    }
        }
        if (s == 4) {
            for (int i = t; i >= 0; i--)
                for (int ii = i; ii >= 0; ii--)
                    for (int j = ii; j >= 0; j--)
                        for (int jj = j; jj >= 0; jj--) {
                            if (i + ii + j + jj == n) {
                                result[index] = String.valueOf(i) + String.valueOf(ii) + String.valueOf(j) + String.valueOf(jj);
                                index++;
                            }
                        }
        }
        if (s == 5) {
            for (int i = t; i >= 0; i--)
                for (int ii = i; ii >= 0; ii--)
                    for (int j = ii; j >= 0; j--)
                        for (int jj = j; jj >= 0; jj--)
                            for (int k = jj; k >= 0; k--) {
                                if (i + ii + j + jj + k == n) {
                                    result[index] = String.valueOf(i) + String.valueOf(ii) + String.valueOf(j) + String.valueOf(jj) + String.valueOf(k);
                                    index++;
                                }
                            }
        }
        if (s == 6) {
            for (int i = t; i >= 0; i--)
                for (int ii = i; ii >= 0; ii--)
                    for (int j = ii; j >= 0; j--)
                        for (int jj = j; jj >= 0; jj--)
                            for (int k = jj; k >= 0; k--)
                                for (int kk = k; kk >= 0; kk--) {
                                    if (i + ii + j + jj + k + kk == n) {
                                        result[index] = String.valueOf(i) + String.valueOf(ii) + String.valueOf(j) + String.valueOf(jj) + String.valueOf(k) + String.valueOf(kk);
                                        index++;
                                    }
                                }
        }
        if (s == 7) {
            for (int i = t; i >= 0; i--)
                for (int ii = i; ii >= 0; ii--)
                    for (int j = ii; j >= 0; j--)
                        for (int jj = j; jj >= 0; jj--)
                            for (int k = jj; k >= 0; k--)
                                for (int kk = k; kk >= 0; kk--)
                                    for (int h = kk; h >= 0; h--) {
                                        if (i + ii + j + jj + k + kk + h == n) {
                                            result[index] = String.valueOf(i) + String.valueOf(ii) + String.valueOf(j) + String.valueOf(jj) + String.valueOf(k) + String.valueOf(kk) + String.valueOf(h);
                                            index++;
                                        }
                                    }
        }
        if (s == 8) {
            for (int i = t; i >= 0; i--)
                for (int ii = i; ii >= 0; ii--)
                    for (int j = ii; j >= 0; j--)
                        for (int jj = j; jj >= 0; jj--)
                            for (int k = jj; k >= 0; k--)
                                for (int kk = k; kk >= 0; kk--)
                                    for (int h = kk; h >= 0; h--)
                                        for (int hh = h; hh >= 0; hh--) {
                                            if (i + ii + j + jj + k + kk + h + hh == n) {
                                                result[index] = String.valueOf(i) + String.valueOf(ii) + String.valueOf(j) + String.valueOf(jj) + String.valueOf(k) + String.valueOf(kk) + String.valueOf(h) + String.valueOf(hh);
                                                index++;
                                            }
                                        }
        }


//        for(int i=0; i<total_number; i++)
//                System.out.println(result[i]);
        return result;
    }

    int permutation_size(String nc, int t, String[] gn) {
        int total_column = 0;  //calculate the number of stacks on which there are empty slots
        for (int i = 0; i < nc.length(); i++) {
            //System.out.println(nc.charAt(i));
            if (nc.charAt(i) > '0')
                total_column++;
        }
        int index = 0;

        if (total_column == 1) {
            for (int i = 0; i < gn.length; i++) {
                if (nc.charAt(0) == String.valueOf(t).charAt(0))
                    i = gn.length;
                index++;
            }
        }

        if (total_column == 2) {
            for (int i = 0; i < gn.length; i++) {
                int start_point2 = 0;
                if (nc.charAt(0) == nc.charAt(1))
                    start_point2 = i;
                else
                    start_point2 = 0;
                for (int ii = start_point2; ii < gn.length; ii++) {
                    if (nc.charAt(0) == String.valueOf(t).charAt(0))
                        i = gn.length;
                    if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
                        ii = gn.length;
                    }

                    index++;
                }
            }
        }

        if (total_column == 3) {
            //int index=0;
            for (int i = 0; i < gn.length; i++) {
                int start_point2 = 0;
                if (nc.charAt(0) == nc.charAt(1))
                    start_point2 = i;
                else
                    start_point2 = 0;
                for (int ii = start_point2; ii < gn.length; ii++) {
                    int start_point3 = 0;
                    if (nc.charAt(1) == nc.charAt(2))
                        start_point3 = ii;
                    else
                        start_point3 = 0;
                    for (int j = start_point3; j < gn.length; j++) {
                        String first = "";
                        if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
                            first = "*";
                            i = gn.length;
                        } else
                            first = gn[i];
                        String second = "";
                        if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
                            second = "*";
                            ii = gn.length;
                        } else
                            second = gn[ii];
                        String third = "";
                        if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
                            third = "*";
                            j = gn.length;
                        } else
                            third = gn[j];

                        //ss[index] = first+ second+third+tail;
                        index++;
                    }
                }
            }
        }

        if (total_column == 4) {
            //int index=0;
            for (int i = 0; i < gn.length; i++) {
                int start_point2 = 0;
                if (nc.charAt(0) == nc.charAt(1))
                    start_point2 = i;
                else
                    start_point2 = 0;
                for (int ii = start_point2; ii < gn.length; ii++) {
                    int start_point3 = 0;
                    if (nc.charAt(1) == nc.charAt(2))
                        start_point3 = ii;
                    else
                        start_point3 = 0;
                    for (int j = start_point3; j < gn.length; j++) {
                        int start_point4 = 0;
                        if (nc.charAt(2) == nc.charAt(3))
                            start_point4 = j;
                        else
                            start_point4 = 0;
                        for (int jj = start_point4; jj < gn.length; jj++) {
                            String first = "";
                            if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
                                first = "*";
                                i = gn.length;
                            } else
                                first = gn[i];
                            String second = "";
                            if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
                                second = "*";
                                ii = gn.length;
                            } else
                                second = gn[ii];
                            String third = "";
                            if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
                                third = "*";
                                j = gn.length;
                            } else
                                third = gn[j];
                            String fourth = "";
                            if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
                                fourth = "*";
                                jj = gn.length;
                            } else
                                fourth = gn[jj];

                            //ss[index] = first+ second+third+fourth+tail;
                            index++;
                        }
                    }
                }
            }
        }

        if (total_column == 5) {
            //int index=0;
            for (int i = 0; i < gn.length; i++) {
                int start_point2 = 0;
                if (nc.charAt(0) == nc.charAt(1))
                    start_point2 = i;
                else
                    start_point2 = 0;
                for (int ii = start_point2; ii < gn.length; ii++) {
                    int start_point3 = 0;
                    if (nc.charAt(1) == nc.charAt(2))
                        start_point3 = ii;
                    else
                        start_point3 = 0;
                    for (int j = start_point3; j < gn.length; j++) {
                        int start_point4 = 0;
                        if (nc.charAt(2) == nc.charAt(3))
                            start_point4 = j;
                        else
                            start_point4 = 0;
                        for (int jj = start_point4; jj < gn.length; jj++) {
                            int start_point5 = 0;
                            if (nc.charAt(3) == nc.charAt(4))
                                start_point5 = jj;
                            else
                                start_point5 = 0;
                            for (int k = start_point5; k < gn.length; k++) {
                                String first = "";
                                if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
                                    first = "*";
                                    i = gn.length;
                                } else
                                    first = gn[i];
                                String second = "";
                                if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
                                    second = "*";
                                    ii = gn.length;
                                } else
                                    second = gn[ii];
                                String third = "";
                                if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
                                    third = "*";
                                    j = gn.length;
                                } else
                                    third = gn[j];
                                String fourth = "";
                                if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
                                    fourth = "*";
                                    jj = gn.length;
                                } else
                                    fourth = gn[jj];
                                String fifth = "";
                                if (nc.charAt(4) == String.valueOf(t).charAt(0)) {
                                    fifth = "*";
                                    k = gn.length;
                                } else
                                    fifth = gn[k];

                                //ss[index] = first+ second+third+fourth+fifth+tail;
                                index++;
                            }
                        }
                    }
                }
            }
        }

        if (total_column == 6) {
            //int index=0;
            for (int i = 0; i < gn.length; i++) {
                int start_point2 = 0;
                if (nc.charAt(0) == nc.charAt(1))
                    start_point2 = i;
                else
                    start_point2 = 0;
                for (int ii = start_point2; ii < gn.length; ii++) {
                    int start_point3 = 0;
                    if (nc.charAt(1) == nc.charAt(2))
                        start_point3 = ii;
                    else
                        start_point3 = 0;
                    for (int j = start_point3; j < gn.length; j++) {
                        int start_point4 = 0;
                        if (nc.charAt(2) == nc.charAt(3))
                            start_point4 = j;
                        else
                            start_point4 = 0;
                        for (int jj = start_point4; jj < gn.length; jj++) {
                            int start_point5 = 0;
                            if (nc.charAt(3) == nc.charAt(4))
                                start_point5 = jj;
                            else
                                start_point5 = 0;
                            for (int k = start_point5; k < gn.length; k++) {
                                int start_point6 = 0;
                                if (nc.charAt(4) == nc.charAt(5))
                                    start_point6 = k;
                                else
                                    start_point6 = 0;
                                for (int kk = start_point6; kk < gn.length; kk++) {
                                    String first = "";
                                    if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
                                        first = "*";
                                        i = gn.length;
                                    } else
                                        first = gn[i];
                                    String second = "";
                                    if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
                                        second = "*";
                                        ii = gn.length;
                                    } else
                                        second = gn[ii];
                                    String third = "";
                                    if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
                                        third = "*";
                                        j = gn.length;
                                    } else
                                        third = gn[j];
                                    String fourth = "";
                                    if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
                                        fourth = "*";
                                        jj = gn.length;
                                    } else
                                        fourth = gn[jj];
                                    String fifth = "";
                                    if (nc.charAt(4) == String.valueOf(t).charAt(0)) {
                                        fifth = "*";
                                        k = gn.length;
                                    } else
                                        fifth = gn[k];
                                    String sixth = "";
                                    if (nc.charAt(5) == String.valueOf(t).charAt(0)) {
                                        sixth = "*";
                                        kk = gn.length;
                                    } else
                                        sixth = gn[kk];

                                    //ss[index] = first+ second+third+fourth+fifth+sixth+tail;
                                    index++;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (total_column == 7) {
            //int index=0;
            for (int i = 0; i < gn.length; i++) {
                int start_point2 = 0;
                if (nc.charAt(0) == nc.charAt(1))
                    start_point2 = i;
                else
                    start_point2 = 0;
                for (int ii = start_point2; ii < gn.length; ii++) {
                    int start_point3 = 0;
                    if (nc.charAt(1) == nc.charAt(2))
                        start_point3 = ii;
                    else
                        start_point3 = 0;
                    for (int j = start_point3; j < gn.length; j++) {
                        int start_point4 = 0;
                        if (nc.charAt(2) == nc.charAt(3))
                            start_point4 = j;
                        else
                            start_point4 = 0;
                        for (int jj = start_point4; jj < gn.length; jj++) {
                            int start_point5 = 0;
                            if (nc.charAt(3) == nc.charAt(4))
                                start_point5 = jj;
                            else
                                start_point5 = 0;
                            for (int k = start_point5; k < gn.length; k++) {
                                int start_point6 = 0;
                                if (nc.charAt(4) == nc.charAt(5))
                                    start_point6 = k;
                                else
                                    start_point6 = 0;
                                for (int kk = start_point6; kk < gn.length; kk++) {
                                    int start_point7 = 0;
                                    if (nc.charAt(5) == nc.charAt(6))
                                        start_point7 = kk;
                                    else
                                        start_point7 = 0;
                                    for (int h = start_point7; h < gn.length; h++) {
                                        String first = "";
                                        if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
                                            first = "*";
                                            i = gn.length;
                                        } else
                                            first = gn[i];
                                        String second = "";
                                        if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
                                            second = "*";
                                            ii = gn.length;
                                        } else
                                            second = gn[ii];
                                        String third = "";
                                        if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
                                            third = "*";
                                            j = gn.length;
                                        } else
                                            third = gn[j];
                                        String fourth = "";
                                        if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
                                            fourth = "*";
                                            jj = gn.length;
                                        } else
                                            fourth = gn[jj];
                                        String fifth = "";
                                        if (nc.charAt(4) == String.valueOf(t).charAt(0)) {
                                            fifth = "*";
                                            k = gn.length;
                                        } else
                                            fifth = gn[k];
                                        String sixth = "";
                                        if (nc.charAt(5) == String.valueOf(t).charAt(0)) {
                                            sixth = "*";
                                            kk = gn.length;
                                        } else
                                            sixth = gn[kk];
                                        String seventh = "";
                                        if (nc.charAt(6) == String.valueOf(t).charAt(0)) {
                                            seventh = "*";
                                            h = gn.length;
                                        } else
                                            seventh = gn[h];

                                        //ss[index] = first+ second+third+fourth+fifth+sixth+seventh+tail;
                                        index++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (total_column == 8) {
            //int index=0;
            for (int i = 0; i < gn.length; i++) {
                int start_point2 = 0;
                if (nc.charAt(0) == nc.charAt(1))
                    start_point2 = i;
                else
                    start_point2 = 0;
                for (int ii = start_point2; ii < gn.length; ii++) {
                    int start_point3 = 0;
                    if (nc.charAt(1) == nc.charAt(2))
                        start_point3 = ii;
                    else
                        start_point3 = 0;
                    for (int j = start_point3; j < gn.length; j++) {
                        int start_point4 = 0;
                        if (nc.charAt(2) == nc.charAt(3))
                            start_point4 = j;
                        else
                            start_point4 = 0;
                        for (int jj = start_point4; jj < gn.length; jj++) {
                            int start_point5 = 0;
                            if (nc.charAt(3) == nc.charAt(4))
                                start_point5 = jj;
                            else
                                start_point5 = 0;
                            for (int k = start_point5; k < gn.length; k++) {
                                int start_point6 = 0;
                                if (nc.charAt(4) == nc.charAt(5))
                                    start_point6 = k;
                                else
                                    start_point6 = 0;
                                for (int kk = start_point6; kk < gn.length; kk++) {
                                    int start_point7 = 0;
                                    if (nc.charAt(5) == nc.charAt(6))
                                        start_point7 = kk;
                                    else
                                        start_point7 = 0;
                                    for (int h = start_point7; h < gn.length; h++) {
                                        int start_point8 = 0;
                                        if (nc.charAt(6) == nc.charAt(7))
                                            start_point8 = h;
                                        else
                                            start_point8 = 0;
                                        for (int hh = start_point8; hh < gn.length; hh++) {
                                            String first = "";
                                            if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
                                                first = "*";
                                                i = gn.length;
                                            } else
                                                first = gn[i];
                                            String second = "";
                                            if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
                                                second = "*";
                                                ii = gn.length;
                                            } else
                                                second = gn[ii];
                                            String third = "";
                                            if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
                                                third = "*";
                                                j = gn.length;
                                            } else
                                                third = gn[j];
                                            String fourth = "";
                                            if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
                                                fourth = "*";
                                                jj = gn.length;
                                            } else
                                                fourth = gn[jj];
                                            String fifth = "";
                                            if (nc.charAt(4) == String.valueOf(t).charAt(0)) {
                                                fifth = "*";
                                                k = gn.length;
                                            } else
                                                fifth = gn[k];
                                            String sixth = "";
                                            if (nc.charAt(5) == String.valueOf(t).charAt(0)) {
                                                sixth = "*";
                                                kk = gn.length;
                                            } else
                                                sixth = gn[kk];
                                            String seventh = "";
                                            if (nc.charAt(6) == String.valueOf(t).charAt(0)) {
                                                seventh = "*";
                                                h = gn.length;
                                            } else
                                                seventh = gn[h];
                                            String eighth = "";
                                            if (nc.charAt(7) == String.valueOf(t).charAt(0)) {
                                                eighth = "*";
                                                hh = gn.length;
                                            } else
                                                eighth = gn[hh];

                                            //ss[index] = first+ second+third+fourth+fifth+sixth+seventh+eighth+tail;
                                            index++;
                                        }
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

    String[] permutation(String nc, int t, String[] gn) {

        String[] ss = new String[permutation_size(nc, t, gn)];
        int total_column = 0;  //calculate the number of stacks on which there are empty slots
        for (int i = 0; i < nc.length(); i++) {
            //System.out.println(nc.charAt(i));
            if (nc.charAt(i) > '0')
                total_column++;
        }
        //System.out.println(total_column);
        String tail = "";   //the states on the full stack are "0000"
        for (int j = 0; j < nc.length() - total_column; j++)
            tail = tail + "0";
        //System.out.println(tail);

        if (total_column == 1) {
            int index = 0;
            for (int i = 0; i < gn.length; i++) {
                String first = "";
                if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
                    first = "*";
                    i = gn.length;
                } else
                    first = gn[i];
                ss[index] = first + tail;
                index++;
            }
        }

        if (total_column == 2) {
            int index = 0;
            for (int i = 0; i < gn.length; i++) {
                int start_point2 = 0;
                if (nc.charAt(0) == nc.charAt(1))
                    start_point2 = i;
                else
                    start_point2 = 0;
                for (int ii = start_point2; ii < gn.length; ii++) {
                    String first = "";
                    if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
                        first = "*";
                        i = gn.length;
                    } else
                        first = gn[i];
                    String second = "";
                    if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
                        second = "*";
                        ii = gn.length;
                    } else
                        second = gn[ii];


                    ss[index] = first + second + tail;
                    index++;
                }
            }
        }

        if (total_column == 3) {
            int index = 0;
            for (int i = 0; i < gn.length; i++) {
                int start_point2 = 0;
                if (nc.charAt(0) == nc.charAt(1))
                    start_point2 = i;
                else
                    start_point2 = 0;
                for (int ii = start_point2; ii < gn.length; ii++) {
                    int start_point3 = 0;
                    if (nc.charAt(1) == nc.charAt(2))
                        start_point3 = ii;
                    else
                        start_point3 = 0;
                    for (int j = start_point3; j < gn.length; j++) {
                        String first = "";
                        if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
                            first = "*";
                            i = gn.length;
                        } else
                            first = gn[i];
                        String second = "";
                        if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
                            second = "*";
                            ii = gn.length;
                        } else
                            second = gn[ii];
                        String third = "";
                        if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
                            third = "*";
                            j = gn.length;
                        } else
                            third = gn[j];

                        ss[index] = first + second + third + tail;
                        index++;
                    }
                }
            }
        }

        if (total_column == 4) {
            int index = 0;
            for (int i = 0; i < gn.length; i++) {
                int start_point2 = 0;
                if (nc.charAt(0) == nc.charAt(1))
                    start_point2 = i;
                else
                    start_point2 = 0;
                for (int ii = start_point2; ii < gn.length; ii++) {
                    int start_point3 = 0;
                    if (nc.charAt(1) == nc.charAt(2))
                        start_point3 = ii;
                    else
                        start_point3 = 0;
                    for (int j = start_point3; j < gn.length; j++) {
                        int start_point4 = 0;
                        if (nc.charAt(2) == nc.charAt(3))
                            start_point4 = j;
                        else
                            start_point4 = 0;
                        for (int jj = start_point4; jj < gn.length; jj++) {
                            String first = "";
                            if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
                                first = "*";
                                i = gn.length;
                            } else
                                first = gn[i];
                            String second = "";
                            if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
                                second = "*";
                                ii = gn.length;
                            } else
                                second = gn[ii];
                            String third = "";
                            if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
                                third = "*";
                                j = gn.length;
                            } else
                                third = gn[j];
                            String fourth = "";
                            if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
                                fourth = "*";
                                jj = gn.length;
                            } else
                                fourth = gn[jj];

                            ss[index] = first + second + third + fourth + tail;
                            index++;
                        }
                    }
                }
            }
        }

        if (total_column == 5) {
            int index = 0;
            for (int i = 0; i < gn.length; i++) {
                int start_point2 = 0;
                if (nc.charAt(0) == nc.charAt(1))
                    start_point2 = i;
                else
                    start_point2 = 0;
                for (int ii = start_point2; ii < gn.length; ii++) {
                    int start_point3 = 0;
                    if (nc.charAt(1) == nc.charAt(2))
                        start_point3 = ii;
                    else
                        start_point3 = 0;
                    for (int j = start_point3; j < gn.length; j++) {
                        int start_point4 = 0;
                        if (nc.charAt(2) == nc.charAt(3))
                            start_point4 = j;
                        else
                            start_point4 = 0;
                        for (int jj = start_point4; jj < gn.length; jj++) {
                            int start_point5 = 0;
                            if (nc.charAt(3) == nc.charAt(4))
                                start_point5 = jj;
                            else
                                start_point5 = 0;
                            for (int k = start_point5; k < gn.length; k++) {
                                String first = "";
                                if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
                                    first = "*";
                                    i = gn.length;
                                } else
                                    first = gn[i];
                                String second = "";
                                if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
                                    second = "*";
                                    ii = gn.length;
                                } else
                                    second = gn[ii];
                                String third = "";
                                if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
                                    third = "*";
                                    j = gn.length;
                                } else
                                    third = gn[j];
                                String fourth = "";
                                if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
                                    fourth = "*";
                                    jj = gn.length;
                                } else
                                    fourth = gn[jj];
                                String fifth = "";
                                if (nc.charAt(4) == String.valueOf(t).charAt(0)) {
                                    fifth = "*";
                                    k = gn.length;
                                } else
                                    fifth = gn[k];

                                ss[index] = first + second + third + fourth + fifth + tail;
                                index++;
                            }
                        }
                    }
                }
            }
        }

        if (total_column == 6) {
            int index = 0;
            for (int i = 0; i < gn.length; i++) {
                int start_point2 = 0;
                if (nc.charAt(0) == nc.charAt(1))
                    start_point2 = i;
                else
                    start_point2 = 0;
                for (int ii = start_point2; ii < gn.length; ii++) {
                    int start_point3 = 0;
                    if (nc.charAt(1) == nc.charAt(2))
                        start_point3 = ii;
                    else
                        start_point3 = 0;
                    for (int j = start_point3; j < gn.length; j++) {
                        int start_point4 = 0;
                        if (nc.charAt(2) == nc.charAt(3))
                            start_point4 = j;
                        else
                            start_point4 = 0;
                        for (int jj = start_point4; jj < gn.length; jj++) {
                            int start_point5 = 0;
                            if (nc.charAt(3) == nc.charAt(4))
                                start_point5 = jj;
                            else
                                start_point5 = 0;
                            for (int k = start_point5; k < gn.length; k++) {
                                int start_point6 = 0;
                                if (nc.charAt(4) == nc.charAt(5))
                                    start_point6 = k;
                                else
                                    start_point6 = 0;
                                for (int kk = start_point6; kk < gn.length; kk++) {
                                    String first = "";
                                    if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
                                        first = "*";
                                        i = gn.length;
                                    } else
                                        first = gn[i];
                                    String second = "";
                                    if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
                                        second = "*";
                                        ii = gn.length;
                                    } else
                                        second = gn[ii];
                                    String third = "";
                                    if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
                                        third = "*";
                                        j = gn.length;
                                    } else
                                        third = gn[j];
                                    String fourth = "";
                                    if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
                                        fourth = "*";
                                        jj = gn.length;
                                    } else
                                        fourth = gn[jj];
                                    String fifth = "";
                                    if (nc.charAt(4) == String.valueOf(t).charAt(0)) {
                                        fifth = "*";
                                        k = gn.length;
                                    } else
                                        fifth = gn[k];
                                    String sixth = "";
                                    if (nc.charAt(5) == String.valueOf(t).charAt(0)) {
                                        sixth = "*";
                                        kk = gn.length;
                                    } else
                                        sixth = gn[kk];

                                    ss[index] = first + second + third + fourth + fifth + sixth + tail;
                                    index++;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (total_column == 7) {
            int index = 0;
            for (int i = 0; i < gn.length; i++) {
                int start_point2 = 0;
                if (nc.charAt(0) == nc.charAt(1))
                    start_point2 = i;
                else
                    start_point2 = 0;
                for (int ii = start_point2; ii < gn.length; ii++) {
                    int start_point3 = 0;
                    if (nc.charAt(1) == nc.charAt(2))
                        start_point3 = ii;
                    else
                        start_point3 = 0;
                    for (int j = start_point3; j < gn.length; j++) {
                        int start_point4 = 0;
                        if (nc.charAt(2) == nc.charAt(3))
                            start_point4 = j;
                        else
                            start_point4 = 0;
                        for (int jj = start_point4; jj < gn.length; jj++) {
                            int start_point5 = 0;
                            if (nc.charAt(3) == nc.charAt(4))
                                start_point5 = jj;
                            else
                                start_point5 = 0;
                            for (int k = start_point5; k < gn.length; k++) {
                                int start_point6 = 0;
                                if (nc.charAt(4) == nc.charAt(5))
                                    start_point6 = k;
                                else
                                    start_point6 = 0;
                                for (int kk = start_point6; kk < gn.length; kk++) {
                                    int start_point7 = 0;
                                    if (nc.charAt(5) == nc.charAt(6))
                                        start_point7 = kk;
                                    else
                                        start_point7 = 0;
                                    for (int h = start_point7; h < gn.length; h++) {
                                        String first = "";
                                        if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
                                            first = "*";
                                            i = gn.length;
                                        } else
                                            first = gn[i];
                                        String second = "";
                                        if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
                                            second = "*";
                                            ii = gn.length;
                                        } else
                                            second = gn[ii];
                                        String third = "";
                                        if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
                                            third = "*";
                                            j = gn.length;
                                        } else
                                            third = gn[j];
                                        String fourth = "";
                                        if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
                                            fourth = "*";
                                            jj = gn.length;
                                        } else
                                            fourth = gn[jj];
                                        String fifth = "";
                                        if (nc.charAt(4) == String.valueOf(t).charAt(0)) {
                                            fifth = "*";
                                            k = gn.length;
                                        } else
                                            fifth = gn[k];
                                        String sixth = "";
                                        if (nc.charAt(5) == String.valueOf(t).charAt(0)) {
                                            sixth = "*";
                                            kk = gn.length;
                                        } else
                                            sixth = gn[kk];
                                        String seventh = "";
                                        if (nc.charAt(6) == String.valueOf(t).charAt(0)) {
                                            seventh = "*";
                                            h = gn.length;
                                        } else
                                            seventh = gn[h];

                                        ss[index] = first + second + third + fourth + fifth + sixth + seventh + tail;
                                        index++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (total_column == 8) {
            int index = 0;
            for (int i = 0; i < gn.length; i++) {
                int start_point2 = 0;
                if (nc.charAt(0) == nc.charAt(1))
                    start_point2 = i;
                else
                    start_point2 = 0;
                for (int ii = start_point2; ii < gn.length; ii++) {
                    int start_point3 = 0;
                    if (nc.charAt(1) == nc.charAt(2))
                        start_point3 = ii;
                    else
                        start_point3 = 0;
                    for (int j = start_point3; j < gn.length; j++) {
                        int start_point4 = 0;
                        if (nc.charAt(2) == nc.charAt(3))
                            start_point4 = j;
                        else
                            start_point4 = 0;
                        for (int jj = start_point4; jj < gn.length; jj++) {
                            int start_point5 = 0;
                            if (nc.charAt(3) == nc.charAt(4))
                                start_point5 = jj;
                            else
                                start_point5 = 0;
                            for (int k = start_point5; k < gn.length; k++) {
                                int start_point6 = 0;
                                if (nc.charAt(4) == nc.charAt(5))
                                    start_point6 = k;
                                else
                                    start_point6 = 0;
                                for (int kk = start_point6; kk < gn.length; kk++) {
                                    int start_point7 = 0;
                                    if (nc.charAt(5) == nc.charAt(6))
                                        start_point7 = kk;
                                    else
                                        start_point7 = 0;
                                    for (int h = start_point7; h < gn.length; h++) {
                                        int start_point8 = 0;
                                        if (nc.charAt(6) == nc.charAt(7))
                                            start_point8 = h;
                                        else
                                            start_point8 = 0;
                                        for (int hh = start_point8; hh < gn.length; hh++) {
                                            String first = "";
                                            if (nc.charAt(0) == String.valueOf(t).charAt(0)) {
                                                first = "*";
                                                i = gn.length;
                                            } else
                                                first = gn[i];
                                            String second = "";
                                            if (nc.charAt(1) == String.valueOf(t).charAt(0)) {
                                                second = "*";
                                                ii = gn.length;
                                            } else
                                                second = gn[ii];
                                            String third = "";
                                            if (nc.charAt(2) == String.valueOf(t).charAt(0)) {
                                                third = "*";
                                                j = gn.length;
                                            } else
                                                third = gn[j];
                                            String fourth = "";
                                            if (nc.charAt(3) == String.valueOf(t).charAt(0)) {
                                                fourth = "*";
                                                jj = gn.length;
                                            } else
                                                fourth = gn[jj];
                                            String fifth = "";
                                            if (nc.charAt(4) == String.valueOf(t).charAt(0)) {
                                                fifth = "*";
                                                k = gn.length;
                                            } else
                                                fifth = gn[k];
                                            String sixth = "";
                                            if (nc.charAt(5) == String.valueOf(t).charAt(0)) {
                                                sixth = "*";
                                                kk = gn.length;
                                            } else
                                                sixth = gn[kk];
                                            String seventh = "";
                                            if (nc.charAt(6) == String.valueOf(t).charAt(0)) {
                                                seventh = "*";
                                                h = gn.length;
                                            } else
                                                seventh = gn[h];
                                            String eighth = "";
                                            if (nc.charAt(7) == String.valueOf(t).charAt(0)) {
                                                eighth = "*";
                                                hh = gn.length;
                                            } else
                                                eighth = gn[hh];

                                            ss[index] = first + second + third + fourth + fifth + sixth + seventh + eighth + tail;
                                            index++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


//        for(int i=0; i<ss.length; i++)
//            System.out.println(ss[i]);

        return ss;

    }

    void dynamic_programming(int s, int t, String[] gn, double[] ratio) {
        // s: the number of stacks
        // t: the number of tiers
        // g: the number of weight groups
        int n = 0;
        String[] nc_pre;
        String[] sc_pre;
        double[] va_pre;
        int tn_pre;
        String[] nc_cur;
        String[] sc_cur;
        double[] va_cur;
        int tn_cur;

        tn_pre = 1;
        nc_pre = new String[tn_pre];
        sc_pre = new String[tn_pre];
        va_pre = new double[tn_pre];

        String temp = "";
        for (int j = 0; j < s; j++)
            temp = temp + "0";
        nc_pre[0] = temp;
        sc_pre[0] = temp;
        va_pre[0] = 0.0;
        state_size = 0;
        while (n < s * t) {
            n = n + 1;
            //the total dimensiion for the case when the number of empty slots equals n
            String[] nc = solution_space(s, t, n);
            tn_cur = dimension_size(s, t, n, gn, nc);
            state_size = state_size + tn_cur;
            nc_cur = new String[tn_cur];
            sc_cur = new String[tn_cur];
            va_cur = new double[tn_cur];
            int index = 0;
            for (int i = 0; i < nc.length; i++) {
                String[] sc = permutation(nc[i], t, gn);
                for (int j = 0; j < sc.length; j++) {
                    nc_cur[index] = nc[i];
                    sc_cur[index] = sc[j];
                    va_cur[index] = calculate_value(t, nc[i], sc[j], gn, nc_pre, sc_pre, va_pre, ratio);
                    if (n == s * t)
                        final_value = va_cur[index];
                    index++;
                }
            }


            tn_pre = tn_cur;
            nc_pre = new String[tn_pre];
            sc_pre = new String[tn_pre];
            va_pre = new double[tn_pre];
            for (int i = 0; i < tn_pre; i++) {
                nc_pre[i] = nc_cur[i];
                sc_pre[i] = sc_cur[i];
                va_pre[i] = va_cur[i];
            }

            for (int i = 0; i < nc_cur.length; i++) {
                System.out.println(i + "  " + nc_cur[i] + "  " + sc_cur[i] + " " + va_cur[i]);
            }

        }


    }

    void dynamic_programming(int s, int t, String[] gn, double[] ratio, boolean input) {
        // s: the number of stacks
        // t: the number of tiers
        // g: the number of weight groups
        int n = 0;
        String[] nc_pre;
        String[] sc_pre;
        double[] va_pre;
        int tn_pre;
        String[] nc_cur;
        String[] sc_cur;
        double[] va_cur;
        int tn_cur;

        tn_pre = 1;
        nc_pre = new String[tn_pre];
        sc_pre = new String[tn_pre];
        va_pre = new double[tn_pre];

        String temp = "";
        for (int j = 0; j < s; j++)
            temp = temp + "0";
        nc_pre[0] = temp;
        sc_pre[0] = temp;
        va_pre[0] = 0.0;
        state_size = 0;

        myproperties p = new myproperties();
        try {
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "store_value" + String.valueOf(s) + String.valueOf(t) + String.valueOf(gn.length) + ".txt", false);
            PrintWriter write = new PrintWriter(fw);
            while (n < s * t) {
                n = n + 1;
                //the total dimensiion for the case when the number of empty slots equals n
                String[] nc = solution_space(s, t, n);
                tn_cur = dimension_size(s, t, n, gn, nc);
                state_size = state_size + tn_cur;
                nc_cur = new String[tn_cur];
                sc_cur = new String[tn_cur];
                va_cur = new double[tn_cur];
                int index = 0;
                for (int i = 0; i < nc.length; i++) {
                    String[] sc = permutation(nc[i], t, gn);
                    for (int j = 0; j < sc.length; j++) {
                        nc_cur[index] = nc[i];
                        sc_cur[index] = sc[j];
                        va_cur[index] = calculate_value(t, nc[i], sc[j], gn, nc_pre, sc_pre, va_pre, ratio);
                        write.print(nc_cur[index] + "\t" + sc_cur[index] + "\t" + va_cur[index] + "\t");
                        for (int k = 0; k < gn.length; k++)
                            write.print(next_weight_group[k] + "\t" + next_state[k][0] + "\t" + next_state[k][1] + "\t" + next_value[k] + "\t" + tier_no[k] + "\t" + state_number_chosen[k] + "\t" + state_weight_chosen[k] + "\t");
                        write.println();
                        if (n == s * t)
                            final_value = va_cur[index];
                        index++;
                    }
                }

                tn_pre = tn_cur;
                nc_pre = new String[tn_pre];
                sc_pre = new String[tn_pre];
                va_pre = new double[tn_pre];
                for (int i = 0; i < tn_pre; i++) {
                    nc_pre[i] = nc_cur[i];
                    sc_pre[i] = sc_cur[i];
                    va_pre[i] = va_cur[i];
                }

                for (int i = 0; i < nc_cur.length; i++) {
                    System.out.println(i + "  " + nc_cur[i] + "  " + sc_cur[i] + " " + va_cur[i]);
                }

            }
            fw.close();
            write.close();
        } catch (IOException f) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("error", f.toString());
        }


    }

    void dynamic_programming_simplified(int s, int t, String[] gn, double[] ratio, boolean input) {
        // s: the number of stacks
        // t: the number of tiers
        // g: the number of weight groups
        int n = 0;
        String[] nc_pre;
        String[] sc_pre;
        double[] va_pre;
        int tn_pre;
        String[] nc_cur;
        String[] sc_cur;
        double[] va_cur;
        int tn_cur;

        tn_pre = 1;
        nc_pre = new String[tn_pre];
        sc_pre = new String[tn_pre];
        va_pre = new double[tn_pre];

        String temp = "";
        for (int j = 0; j < s; j++)
            temp = temp + "0";
        nc_pre[0] = temp;
        sc_pre[0] = temp;
        va_pre[0] = 0.0;
        state_size = 0;

        myproperties p = new myproperties();
        try {
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "store_value" + String.valueOf(s) + String.valueOf(t) + String.valueOf(gn.length) + ".txt", false);
            PrintWriter write = new PrintWriter(fw);
            while (n < s * t) {
                n = n + 1;
                //the total dimensiion for the case when the number of empty slots equals n
                String[] nc = solution_space(s, t, n);
                tn_cur = dimension_size(s, t, n, gn, nc);
                state_size = state_size + tn_cur;
                nc_cur = new String[tn_cur];
                sc_cur = new String[tn_cur];
                va_cur = new double[tn_cur];
                int index = 0;
                for (int i = 0; i < nc.length; i++) {
                    String[] sc = permutation(nc[i], t, gn);
                    for (int j = 0; j < sc.length; j++) {
                        nc_cur[index] = nc[i];
                        sc_cur[index] = sc[j];
                        va_cur[index] = calculate_value(t, nc[i], sc[j], gn, nc_pre, sc_pre, va_pre, ratio);
                        write.print(nc_cur[index] + sc_cur[index] + "\t");
                        for (int k = 0; k < gn.length; k++)
                            write.print(next_state[k][0] + next_state[k][1] + "\t" + state_number_chosen[k] + "\t" + state_weight_chosen[k] + "\t");
                        write.println();
                        if (n == s * t)
                            final_value = va_cur[index];
                        index++;
                    }
                }

                tn_pre = tn_cur;
                nc_pre = new String[tn_pre];
                sc_pre = new String[tn_pre];
                va_pre = new double[tn_pre];
                for (int i = 0; i < tn_pre; i++) {
                    nc_pre[i] = nc_cur[i];
                    sc_pre[i] = sc_cur[i];
                    va_pre[i] = va_cur[i];
                }

                for (int i = 0; i < nc_cur.length; i++) {
                    System.out.println(i + "  " + nc_cur[i] + "  " + sc_cur[i] + " " + va_cur[i]);
                }

            }
            fw.close();
            write.close();
        } catch (IOException f) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("error", f.toString());
        }


    }


}

class Calculate_total_rehandle_prepare {
    String[][] current_state;
    double[] current_value;
    String[][] next_state;
    String[][] next_weight_group;
    double[][] next_value;
    int[][] tier_no;
    int[][] state_number_chosen;
    String[][] state_weight_chosen;
    int size;

    void datainput(String filename, String[] gn) {
        myproperties p = new myproperties();
        String path = p.getproperties("path");

        Input input = new Input();
        String[][] dataarray = input.readdata(path + filename);

        size = dataarray.length;
        current_state = new String[dataarray.length][2];
        current_value = new double[dataarray.length];
        next_state = new String[dataarray.length][2 * gn.length];
        next_weight_group = new String[dataarray.length][gn.length];
        next_value = new double[dataarray.length][gn.length];
        tier_no = new int[dataarray.length][gn.length];
        state_number_chosen = new int[dataarray.length][gn.length];
        state_weight_chosen = new String[dataarray.length][gn.length];

        for (int i = 0; i < dataarray.length; i++) {
            current_state[i][0] = dataarray[i][0];
            current_state[i][1] = dataarray[i][1];
            current_value[i] = Double.valueOf(dataarray[i][2]);
            for (int k = 0; k < gn.length; k++) {
                next_weight_group[i][k] = dataarray[i][7 * k + 3];
                next_state[i][2 * k + 0] = dataarray[i][7 * k + 4];
                next_state[i][2 * k + 1] = dataarray[i][7 * k + 5];
                next_value[i][k] = Double.valueOf(dataarray[i][7 * k + 6]);
                tier_no[i][k] = Integer.valueOf(dataarray[i][7 * k + 7]);
                state_number_chosen[i][k] = Integer.valueOf(dataarray[i][7 * k + 8]);
                state_weight_chosen[i][k] = dataarray[i][7 * k + 9];
            }
        }
    }

    void datainput_simplified(String filename, String[] gn) {
        myproperties p = new myproperties();
        String path = p.getproperties("path");

        Input input = new Input();
        String[][] dataarray = input.readdata(path + filename);

        size = dataarray.length;
        current_state = new String[dataarray.length][1];
        next_state = new String[dataarray.length][gn.length];
        state_number_chosen = new int[dataarray.length][gn.length];
        state_weight_chosen = new String[dataarray.length][gn.length];

        for (int i = 0; i < dataarray.length; i++) {
            current_state[i][0] = dataarray[i][0];
            for (int k = 0; k < gn.length; k++) {
                next_state[i][k] = dataarray[i][3 * k + 1];
                state_number_chosen[i][k] = Integer.valueOf(dataarray[i][3 * k + 2]);
                state_weight_chosen[i][k] = dataarray[i][3 * k + 3];
            }
        }
    }

    String[] generate_string(int s, int t, String[] gn) {
        int accumulation = 1;
        for (int i = 0; i < s * t; i++) {
            accumulation = accumulation * gn.length;
            if (accumulation > 2000000) {
                accumulation = 2000000;
                break;
            }
        }

        String[] permutation = new String[accumulation];
        int index = 0;


        if (s * t == 28)
            for (int i = 0; i < gn.length; i++) {
                for (int i1 = 0; i1 < gn.length; i1++) {
                    for (int i2 = 0; i2 < gn.length; i2++) {
                        for (int i3 = 0; i3 < gn.length; i3++) {
                            for (int i4 = 0; i4 < gn.length; i4++) {
                                for (int i5 = 0; i5 < gn.length; i5++) {
                                    for (int i6 = 0; i6 < gn.length; i6++) {
                                        for (int i7 = 0; i7 < gn.length; i7++) {
                                            for (int i8 = 0; i8 < gn.length; i8++) {
                                                for (int i9 = 0; i9 < gn.length; i9++) {
                                                    for (int j = 0; j < gn.length; j++) {
                                                        for (int j1 = 0; j1 < gn.length; j1++) {
                                                            for (int j2 = 0; j2 < gn.length; j2++) {
                                                                for (int j3 = 0; j3 < gn.length; j3++) {
                                                                    for (int j4 = 0; j4 < gn.length; j4++) {
                                                                        for (int j5 = 0; j5 < gn.length; j5++) {
                                                                            for (int j6 = 0; j6 < gn.length; j6++) {
                                                                                for (int j7 = 0; j7 < gn.length; j7++) {
                                                                                    for (int j8 = 0; j8 < gn.length; j8++) {
                                                                                        for (int j9 = 0; j9 < gn.length; j9++) {
                                                                                            for (int k = 0; k < gn.length; k++) {
                                                                                                for (int k1 = 0; k1 < gn.length; k1++) {
                                                                                                    for (int k2 = 0; k2 < gn.length; k2++) {
                                                                                                        for (int k3 = 0; k3 < gn.length; k3++) {
                                                                                                            for (int k4 = 0; k4 < gn.length; k4++) {
                                                                                                                for (int k5 = 0; k5 < gn.length; k5++) {
                                                                                                                    for (int k6 = 0; k6 < gn.length; k6++) {
                                                                                                                        for (int k7 = 0; k7 < gn.length; k7++) {
                                                                                                                            if (index < accumulation) {
                                                                                                                                permutation[index] = gn[i] + gn[i1] + gn[i2] + gn[i3] + gn[i4] + gn[i5] + gn[i6] + gn[i7] + gn[i8] + gn[i9] + gn[j] + gn[j1] + gn[j2] + gn[j3] + gn[j4] + gn[j5] + gn[j6] + gn[j7] + gn[j8] + gn[j9] + gn[k] + gn[k1] + gn[k2] + gn[k3] + gn[k4] + gn[k5] + gn[k6] + gn[k7];

                                                                                                                                index++;
                                                                                                                            } else
                                                                                                                                break;
                                                                                                                        }
                                                                                                                        if (index >= accumulation)
                                                                                                                            break;
                                                                                                                    }
                                                                                                                    if (index >= accumulation)
                                                                                                                        break;
                                                                                                                }
                                                                                                                if (index >= accumulation)
                                                                                                                    break;
                                                                                                            }
                                                                                                            if (index >= accumulation)
                                                                                                                break;
                                                                                                        }
                                                                                                        if (index >= accumulation)
                                                                                                            break;
                                                                                                    }
                                                                                                    if (index >= accumulation)
                                                                                                        break;
                                                                                                }
                                                                                                if (index >= accumulation)
                                                                                                    break;
                                                                                            }
                                                                                            if (index >= accumulation)
                                                                                                break;
                                                                                        }
                                                                                        if (index >= accumulation)
                                                                                            break;
                                                                                    }
                                                                                    if (index >= accumulation)
                                                                                        break;
                                                                                }
                                                                                if (index >= accumulation)
                                                                                    break;
                                                                            }
                                                                            if (index >= accumulation)
                                                                                break;
                                                                        }
                                                                        if (index >= accumulation)
                                                                            break;
                                                                    }
                                                                    if (index >= accumulation)
                                                                        break;
                                                                }
                                                                if (index >= accumulation)
                                                                    break;
                                                            }
                                                            if (index >= accumulation)
                                                                break;
                                                        }
                                                        if (index >= accumulation)
                                                            break;
                                                    }
                                                    if (index >= accumulation)
                                                        break;
                                                }
                                                if (index >= accumulation)
                                                    break;
                                            }
                                            if (index >= accumulation)
                                                break;
                                        }
                                        if (index >= accumulation)
                                            break;
                                    }
                                    if (index >= accumulation)
                                        break;
                                }
                                if (index >= accumulation)
                                    break;
                            }
                            if (index >= accumulation)
                                break;
                        }
                        if (index >= accumulation)
                            break;
                    }
                    if (index >= accumulation)
                        break;
                }
                if (index >= accumulation)
                    break;
            }

        if (s * t == 24)
            for (int i = 0; i < gn.length; i++) {
                for (int i1 = 0; i1 < gn.length; i1++) {
                    for (int i2 = 0; i2 < gn.length; i2++) {
                        for (int i3 = 0; i3 < gn.length; i3++) {
                            for (int i4 = 0; i4 < gn.length; i4++) {
                                for (int i5 = 0; i5 < gn.length; i5++) {
                                    for (int i6 = 0; i6 < gn.length; i6++) {
                                        for (int i7 = 0; i7 < gn.length; i7++) {
                                            for (int i8 = 0; i8 < gn.length; i8++) {
                                                for (int i9 = 0; i9 < gn.length; i9++) {
                                                    for (int j = 0; j < gn.length; j++) {
                                                        for (int j1 = 0; j1 < gn.length; j1++) {
                                                            for (int j2 = 0; j2 < gn.length; j2++) {
                                                                for (int j3 = 0; j3 < gn.length; j3++) {
                                                                    for (int j4 = 0; j4 < gn.length; j4++) {
                                                                        for (int j5 = 0; j5 < gn.length; j5++) {
                                                                            for (int j6 = 0; j6 < gn.length; j6++) {
                                                                                for (int j7 = 0; j7 < gn.length; j7++) {
                                                                                    for (int j8 = 0; j8 < gn.length; j8++) {
                                                                                        for (int j9 = 0; j9 < gn.length; j9++) {
                                                                                            for (int k = 0; k < gn.length; k++) {
                                                                                                for (int k1 = 0; k1 < gn.length; k1++) {
                                                                                                    for (int k2 = 0; k2 < gn.length; k2++) {
                                                                                                        for (int k3 = 0; k3 < gn.length; k3++) {
                                                                                                            if (index < accumulation) {
                                                                                                                permutation[index] = gn[i] + gn[i1] + gn[i2] + gn[i3] + gn[i4] + gn[i5] + gn[i6] + gn[i7] + gn[i8] + gn[i9] + gn[j] + gn[j1] + gn[j2] + gn[j3] + gn[j4] + gn[j5] + gn[j6] + gn[j7] + gn[j8] + gn[j9] + gn[k] + gn[k1] + gn[k2] + gn[k3];
                                                                                                                index++;
                                                                                                            } else
                                                                                                                break;
                                                                                                        }
                                                                                                        if (index >= accumulation)
                                                                                                            break;
                                                                                                    }
                                                                                                    if (index >= accumulation)
                                                                                                        break;
                                                                                                }
                                                                                                if (index >= accumulation)
                                                                                                    break;
                                                                                            }
                                                                                            if (index >= accumulation)
                                                                                                break;
                                                                                        }
                                                                                        if (index >= accumulation)
                                                                                            break;
                                                                                    }
                                                                                    if (index >= accumulation)
                                                                                        break;
                                                                                }
                                                                                if (index >= accumulation)
                                                                                    break;
                                                                            }
                                                                            if (index >= accumulation)
                                                                                break;
                                                                        }
                                                                        if (index >= accumulation)
                                                                            break;
                                                                    }
                                                                    if (index >= accumulation)
                                                                        break;
                                                                }
                                                                if (index >= accumulation)
                                                                    break;
                                                            }
                                                            if (index >= accumulation)
                                                                break;
                                                        }
                                                        if (index >= accumulation)
                                                            break;
                                                    }
                                                    if (index >= accumulation)
                                                        break;
                                                }
                                                if (index >= accumulation)
                                                    break;
                                            }
                                            if (index >= accumulation)
                                                break;
                                        }
                                        if (index >= accumulation)
                                            break;
                                    }
                                    if (index >= accumulation)
                                        break;
                                }
                                if (index >= accumulation)
                                    break;
                            }
                            if (index >= accumulation)
                                break;
                        }
                        if (index >= accumulation)
                            break;
                    }
                    if (index >= accumulation)
                        break;
                }
                if (index >= accumulation)
                    break;
            }

        if (s * t == 21)
            for (int i = 0; i < gn.length; i++) {
                for (int i1 = 0; i1 < gn.length; i1++) {
                    for (int i2 = 0; i2 < gn.length; i2++) {
                        for (int i3 = 0; i3 < gn.length; i3++) {
                            for (int i4 = 0; i4 < gn.length; i4++) {
                                for (int i5 = 0; i5 < gn.length; i5++) {
                                    for (int i6 = 0; i6 < gn.length; i6++) {
                                        for (int i7 = 0; i7 < gn.length; i7++) {
                                            for (int i8 = 0; i8 < gn.length; i8++) {
                                                for (int i9 = 0; i9 < gn.length; i9++) {
                                                    for (int j = 0; j < gn.length; j++) {
                                                        for (int j1 = 0; j1 < gn.length; j1++) {
                                                            for (int j2 = 0; j2 < gn.length; j2++) {
                                                                for (int j3 = 0; j3 < gn.length; j3++) {
                                                                    for (int j4 = 0; j4 < gn.length; j4++) {
                                                                        for (int j5 = 0; j5 < gn.length; j5++) {
                                                                            for (int j6 = 0; j6 < gn.length; j6++) {
                                                                                for (int j7 = 0; j7 < gn.length; j7++) {
                                                                                    for (int j8 = 0; j8 < gn.length; j8++) {
                                                                                        for (int j9 = 0; j9 < gn.length; j9++) {
                                                                                            for (int k = 0; k < gn.length; k++) {
                                                                                                if (index < accumulation) {
                                                                                                    permutation[index] = gn[i] + gn[i1] + gn[i2] + gn[i3] + gn[i4] + gn[i5] + gn[i6] + gn[i7] + gn[i8] + gn[i9] + gn[j] + gn[j1] + gn[j2] + gn[j3] + gn[j4] + gn[j5] + gn[j6] + gn[j7] + gn[j8] + gn[j9] + gn[k];
                                                                                                    index++;
                                                                                                } else
                                                                                                    break;
                                                                                            }
                                                                                            if (index >= accumulation)
                                                                                                break;
                                                                                        }
                                                                                        if (index >= accumulation)
                                                                                            break;
                                                                                    }
                                                                                    if (index >= accumulation)
                                                                                        break;
                                                                                }
                                                                                if (index >= accumulation)
                                                                                    break;
                                                                            }
                                                                            if (index >= accumulation)
                                                                                break;
                                                                        }
                                                                        if (index >= accumulation)
                                                                            break;
                                                                    }
                                                                    if (index >= accumulation)
                                                                        break;
                                                                }
                                                                if (index >= accumulation)
                                                                    break;
                                                            }
                                                            if (index >= accumulation)
                                                                break;
                                                        }
                                                        if (index >= accumulation)
                                                            break;
                                                    }
                                                    if (index >= accumulation)
                                                        break;
                                                }
                                                if (index >= accumulation)
                                                    break;
                                            }
                                            if (index >= accumulation)
                                                break;
                                        }
                                        if (index >= accumulation)
                                            break;
                                    }
                                    if (index >= accumulation)
                                        break;
                                }
                                if (index >= accumulation)
                                    break;
                            }
                            if (index >= accumulation)
                                break;
                        }
                        if (index >= accumulation)
                            break;
                    }
                    if (index >= accumulation)
                        break;
                }
                if (index >= accumulation)
                    break;
            }

        if (s * t == 20)
            for (int i = 0; i < gn.length; i++) {
                for (int i1 = 0; i1 < gn.length; i1++) {
                    for (int i2 = 0; i2 < gn.length; i2++) {
                        for (int i3 = 0; i3 < gn.length; i3++) {
                            for (int i4 = 0; i4 < gn.length; i4++) {
                                for (int i5 = 0; i5 < gn.length; i5++) {
                                    for (int i6 = 0; i6 < gn.length; i6++) {
                                        for (int i7 = 0; i7 < gn.length; i7++) {
                                            for (int i8 = 0; i8 < gn.length; i8++) {
                                                for (int i9 = 0; i9 < gn.length; i9++) {
                                                    for (int j = 0; j < gn.length; j++) {
                                                        for (int j1 = 0; j1 < gn.length; j1++) {
                                                            for (int j2 = 0; j2 < gn.length; j2++) {
                                                                for (int j3 = 0; j3 < gn.length; j3++) {
                                                                    for (int j4 = 0; j4 < gn.length; j4++) {
                                                                        for (int j5 = 0; j5 < gn.length; j5++) {
                                                                            for (int j6 = 0; j6 < gn.length; j6++) {
                                                                                for (int j7 = 0; j7 < gn.length; j7++) {
                                                                                    for (int j8 = 0; j8 < gn.length; j8++) {
                                                                                        for (int j9 = 0; j9 < gn.length; j9++) {
                                                                                            if (index < accumulation) {
                                                                                                permutation[index] = gn[i] + gn[i1] + gn[i2] + gn[i3] + gn[i4] + gn[i5] + gn[i6] + gn[i7] + gn[i8] + gn[i9] + gn[j] + gn[j1] + gn[j2] + gn[j3] + gn[j4] + gn[j5] + gn[j6] + gn[j7] + gn[j8] + gn[j9];
                                                                                                index++;
                                                                                            } else
                                                                                                break;
                                                                                        }
                                                                                        if (index >= accumulation)
                                                                                            break;
                                                                                    }
                                                                                    if (index >= accumulation)
                                                                                        break;
                                                                                }
                                                                                if (index >= accumulation)
                                                                                    break;
                                                                            }
                                                                            if (index >= accumulation)
                                                                                break;
                                                                        }
                                                                        if (index >= accumulation)
                                                                            break;
                                                                    }
                                                                    if (index >= accumulation)
                                                                        break;
                                                                }
                                                                if (index >= accumulation)
                                                                    break;
                                                            }
                                                            if (index >= accumulation)
                                                                break;
                                                        }
                                                        if (index >= accumulation)
                                                            break;
                                                    }
                                                    if (index >= accumulation)
                                                        break;
                                                }
                                                if (index >= accumulation)
                                                    break;
                                            }
                                            if (index >= accumulation)
                                                break;
                                        }
                                        if (index >= accumulation)
                                            break;
                                    }
                                    if (index >= accumulation)
                                        break;
                                }
                                if (index >= accumulation)
                                    break;
                            }
                            if (index >= accumulation)
                                break;
                        }
                        if (index >= accumulation)
                            break;
                    }
                    if (index >= accumulation)
                        break;
                }
                if (index >= accumulation)
                    break;
            }

        if (s * t == 18)
            for (int i = 0; i < gn.length; i++) {
                for (int i1 = 0; i1 < gn.length; i1++) {
                    for (int i2 = 0; i2 < gn.length; i2++) {
                        for (int i3 = 0; i3 < gn.length; i3++) {
                            for (int i4 = 0; i4 < gn.length; i4++) {
                                for (int i5 = 0; i5 < gn.length; i5++) {
                                    for (int i6 = 0; i6 < gn.length; i6++) {
                                        for (int i7 = 0; i7 < gn.length; i7++) {
                                            for (int i8 = 0; i8 < gn.length; i8++) {
                                                for (int i9 = 0; i9 < gn.length; i9++) {
                                                    for (int j = 0; j < gn.length; j++) {
                                                        for (int j1 = 0; j1 < gn.length; j1++) {
                                                            for (int j2 = 0; j2 < gn.length; j2++) {
                                                                for (int j3 = 0; j3 < gn.length; j3++) {
                                                                    for (int j4 = 0; j4 < gn.length; j4++) {
                                                                        for (int j5 = 0; j5 < gn.length; j5++) {
                                                                            for (int j6 = 0; j6 < gn.length; j6++) {
                                                                                for (int j7 = 0; j7 < gn.length; j7++) {
                                                                                    if (index < accumulation) {
                                                                                        permutation[index] = gn[i] + gn[i1] + gn[i2] + gn[i3] + gn[i4] + gn[i5] + gn[i6] + gn[i7] + gn[i8] + gn[i9] + gn[j] + gn[j1] + gn[j2] + gn[j3] + gn[j4] + gn[j5] + gn[j6] + gn[j7];
                                                                                        index++;
                                                                                    } else
                                                                                        break;
                                                                                }
                                                                                if (index >= accumulation)
                                                                                    break;
                                                                            }
                                                                            if (index >= accumulation)
                                                                                break;
                                                                        }
                                                                        if (index >= accumulation)
                                                                            break;
                                                                    }
                                                                    if (index >= accumulation)
                                                                        break;
                                                                }
                                                                if (index >= accumulation)
                                                                    break;
                                                            }
                                                            if (index >= accumulation)
                                                                break;
                                                        }
                                                        if (index >= accumulation)
                                                            break;
                                                    }
                                                    if (index >= accumulation)
                                                        break;
                                                }
                                                if (index >= accumulation)
                                                    break;
                                            }
                                            if (index >= accumulation)
                                                break;
                                        }
                                        if (index >= accumulation)
                                            break;
                                    }
                                    if (index >= accumulation)
                                        break;
                                }
                                if (index >= accumulation)
                                    break;
                            }
                            if (index >= accumulation)
                                break;
                        }
                        if (index >= accumulation)
                            break;
                    }
                    if (index >= accumulation)
                        break;
                }
                if (index >= accumulation)
                    break;
            }


        if (s * t == 16)
            for (int i = 0; i < gn.length; i++) {
                for (int i1 = 0; i1 < gn.length; i1++) {
                    for (int i2 = 0; i2 < gn.length; i2++) {
                        for (int i3 = 0; i3 < gn.length; i3++) {
                            for (int i4 = 0; i4 < gn.length; i4++) {
                                for (int i5 = 0; i5 < gn.length; i5++) {
                                    for (int i6 = 0; i6 < gn.length; i6++) {
                                        for (int i7 = 0; i7 < gn.length; i7++) {
                                            for (int i8 = 0; i8 < gn.length; i8++) {
                                                for (int i9 = 0; i9 < gn.length; i9++) {
                                                    for (int j = 0; j < gn.length; j++) {
                                                        for (int j1 = 0; j1 < gn.length; j1++) {
                                                            for (int j2 = 0; j2 < gn.length; j2++) {
                                                                for (int j3 = 0; j3 < gn.length; j3++) {
                                                                    for (int j4 = 0; j4 < gn.length; j4++) {
                                                                        for (int j5 = 0; j5 < gn.length; j5++) {
                                                                            if (index < accumulation) {
                                                                                permutation[index] = gn[i] + gn[i1] + gn[i2] + gn[i3] + gn[i4] + gn[i5] + gn[i6] + gn[i7] + gn[i8] + gn[i9] + gn[j] + gn[j1] + gn[j2] + gn[j3] + gn[j4] + gn[j5];
                                                                                index++;
                                                                            } else
                                                                                break;
                                                                        }
                                                                        if (index >= accumulation)
                                                                            break;
                                                                    }
                                                                    if (index >= accumulation)
                                                                        break;
                                                                }
                                                                if (index >= accumulation)
                                                                    break;
                                                            }
                                                            if (index >= accumulation)
                                                                break;
                                                        }
                                                        if (index >= accumulation)
                                                            break;
                                                    }
                                                    if (index >= accumulation)
                                                        break;
                                                }
                                                if (index >= accumulation)
                                                    break;
                                            }
                                            if (index >= accumulation)
                                                break;
                                        }
                                        if (index >= accumulation)
                                            break;
                                    }
                                    if (index >= accumulation)
                                        break;
                                }
                                if (index >= accumulation)
                                    break;
                            }
                            if (index >= accumulation)
                                break;
                        }
                        if (index >= accumulation)
                            break;
                    }
                    if (index >= accumulation)
                        break;
                }
                if (index >= accumulation)
                    break;
            }


        if (s * t == 15)
            for (int i = 0; i < gn.length; i++) {
                for (int i1 = 0; i1 < gn.length; i1++) {
                    for (int i2 = 0; i2 < gn.length; i2++) {
                        for (int i3 = 0; i3 < gn.length; i3++) {
                            for (int i4 = 0; i4 < gn.length; i4++) {
                                for (int i5 = 0; i5 < gn.length; i5++) {
                                    for (int i6 = 0; i6 < gn.length; i6++) {
                                        for (int i7 = 0; i7 < gn.length; i7++) {
                                            for (int i8 = 0; i8 < gn.length; i8++) {
                                                for (int i9 = 0; i9 < gn.length; i9++) {
                                                    for (int j = 0; j < gn.length; j++) {
                                                        for (int j1 = 0; j1 < gn.length; j1++) {
                                                            for (int j2 = 0; j2 < gn.length; j2++) {
                                                                for (int j3 = 0; j3 < gn.length; j3++) {
                                                                    for (int j4 = 0; j4 < gn.length; j4++) {
                                                                        if (index < accumulation) {
                                                                            permutation[index] = gn[i] + gn[i1] + gn[i2] + gn[i3] + gn[i4] + gn[i5] + gn[i6] + gn[i7] + gn[i8] + gn[i9] + gn[j] + gn[j1] + gn[j2] + gn[j3] + gn[j4];
                                                                            index++;
                                                                        } else
                                                                            break;
                                                                    }
                                                                    if (index >= accumulation)
                                                                        break;
                                                                }
                                                                if (index >= accumulation)
                                                                    break;
                                                            }
                                                            if (index >= accumulation)
                                                                break;
                                                        }
                                                        if (index >= accumulation)
                                                            break;
                                                    }
                                                    if (index >= accumulation)
                                                        break;
                                                }
                                                if (index >= accumulation)
                                                    break;
                                            }
                                            if (index >= accumulation)
                                                break;
                                        }
                                        if (index >= accumulation)
                                            break;
                                    }
                                    if (index >= accumulation)
                                        break;
                                }
                                if (index >= accumulation)
                                    break;
                            }
                            if (index >= accumulation)
                                break;
                        }
                        if (index >= accumulation)
                            break;
                    }
                    if (index >= accumulation)
                        break;
                }
                if (index >= accumulation)
                    break;
            }

        if (s * t == 12)
            for (int i = 0; i < gn.length; i++) {
                for (int i1 = 0; i1 < gn.length; i1++) {
                    for (int i2 = 0; i2 < gn.length; i2++) {
                        for (int i3 = 0; i3 < gn.length; i3++) {
                            for (int i4 = 0; i4 < gn.length; i4++) {
                                for (int i5 = 0; i5 < gn.length; i5++) {
                                    for (int i6 = 0; i6 < gn.length; i6++) {
                                        for (int i7 = 0; i7 < gn.length; i7++) {
                                            for (int i8 = 0; i8 < gn.length; i8++) {
                                                for (int i9 = 0; i9 < gn.length; i9++) {
                                                    for (int j = 0; j < gn.length; j++) {
                                                        for (int j1 = 0; j1 < gn.length; j1++) {
                                                            if (index < accumulation) {
                                                                permutation[index] = gn[i] + gn[i1] + gn[i2] + gn[i3] + gn[i4] + gn[i5] + gn[i6] + gn[i7] + gn[i8] + gn[i9] + gn[j] + gn[j1];
                                                                index++;
                                                            } else
                                                                break;
                                                        }
                                                        if (index >= accumulation)
                                                            break;
                                                    }
                                                    if (index >= accumulation)
                                                        break;
                                                }
                                                if (index >= accumulation)
                                                    break;
                                            }
                                            if (index >= accumulation)
                                                break;
                                        }
                                        if (index >= accumulation)
                                            break;
                                    }
                                    if (index >= accumulation)
                                        break;
                                }
                                if (index >= accumulation)
                                    break;
                            }
                            if (index >= accumulation)
                                break;
                        }
                        if (index >= accumulation)
                            break;
                    }
                    if (index >= accumulation)
                        break;
                }
                if (index >= accumulation)
                    break;
            }

        if (s * t == 9)
            for (int i = 0; i < gn.length; i++) {
                for (int i1 = 0; i1 < gn.length; i1++) {
                    for (int i2 = 0; i2 < gn.length; i2++) {
                        for (int i3 = 0; i3 < gn.length; i3++) {
                            for (int i4 = 0; i4 < gn.length; i4++) {
                                for (int i5 = 0; i5 < gn.length; i5++) {
                                    for (int i6 = 0; i6 < gn.length; i6++) {
                                        for (int i7 = 0; i7 < gn.length; i7++) {
                                            for (int i8 = 0; i8 < gn.length; i8++) {
                                                if (index < accumulation) {
                                                    permutation[index] = gn[i] + gn[i1] + gn[i2] + gn[i3] + gn[i4] + gn[i5] + gn[i6] + gn[i7] + gn[i8];
                                                    index++;
                                                } else
                                                    break;
                                            }
                                            if (index >= accumulation)
                                                break;
                                        }
                                        if (index >= accumulation)
                                            break;
                                    }
                                    if (index >= accumulation)
                                        break;
                                }
                                if (index >= accumulation)
                                    break;
                            }
                            if (index >= accumulation)
                                break;
                        }
                        if (index >= accumulation)
                            break;
                    }
                    if (index >= accumulation)
                        break;
                }
                if (index >= accumulation)
                    break;
            }

        if (s * t == 4)
            for (int i = 0; i < gn.length; i++)
                for (int i1 = 0; i1 < gn.length; i1++)
                    for (int i2 = 0; i2 < gn.length; i2++)
                        for (int i3 = 0; i3 < gn.length; i3++) {
                            permutation[index] = gn[i] + gn[i1] + gn[i2] + gn[i3];
                            index++;
                        }

        return permutation;
    }
}

class Calculate_total_rehandle {
    void calculate_total_rehandle(int s, int t, String[] gn) {
        Calculate_total_rehandle_prepare dataarray = new Calculate_total_rehandle_prepare();
        dataarray.datainput("store_value" + String.valueOf(s) + String.valueOf(t) + ".txt", gn);
        //for(int i=0; i<dataarray.size; i++)
        //System.out.println(i+" "+dataarray.next_value[i][1]);

        String[] string_generate = dataarray.generate_string(s, t, gn);
        double[] total_rehandle = new double[string_generate.length];
        for (int i = 0; i < total_rehandle.length; i++)
            total_rehandle[i] = 0.0;
        double final_total_rehandle = 0.0;
        for (int i = 0; i < string_generate.length; i++) {
            String target = string_generate[i];
            String cur_nc = "";
            String cur_sc = "";
            //the initial state
            for (int k = 0; k < s; k++) {
                cur_nc = cur_nc + String.valueOf(t);
                cur_sc = cur_sc + "*";
            }
            for (int j = 0; j < target.length(); j++) {
                int target_index = 0;
                //find the target index
                for (int k = 0; k < dataarray.size; k++) {
                    if (cur_nc.equals(dataarray.current_state[k][0]) && cur_sc.equals(dataarray.current_state[k][1])) {
                        target_index = k;
                        break;
                    }
                }
                //find the position of the target weight group
                int target_k = 0;
                for (int k = 0; k < gn.length; k++) {
                    if (String.valueOf(target.charAt(j)).equals(gn[k])) {
                        target_k = k;
                        break;
                    }
                }
                cur_nc = dataarray.next_state[target_index][2 * target_k + 0];
                cur_sc = dataarray.next_state[target_index][2 * target_k + 1];
                total_rehandle[i] = total_rehandle[i] + dataarray.next_value[target_index][target_k];
                //System.out.println("next value:   "+cur_nc + "  "+cur_sc+"  "+dataarray.next_value[target_index][target_k]);
            }
            System.out.println(i + "  " + target + "  " + total_rehandle[i]);
            final_total_rehandle = final_total_rehandle + total_rehandle[i];
        }
        System.out.println("total rehandle times:   " + final_total_rehandle);

        myproperties p = new myproperties();
        try {
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "total_rehandel_times" + String.valueOf(s) + String.valueOf(t) + ".txt", false);
            PrintWriter write = new PrintWriter(fw);
            write.println(s + "\t" + t + "\t" + string_generate.length + "\t" + final_total_rehandle + "\t");
            fw.close();
            write.close();
        } catch (IOException f) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("error", f.toString());
        }
    }
}

//using a dynamic indicator
class Evaluating_simulation {
    int total_multiplied_value;
    int total_sequence_size;

    //calculate the expected addition number of relocations
    double[][][] expected_addition(int s, int t) {
        double[][][] addition_relocation = new double[s * t][t + 1][s * t];
        for (int i = 0; i < s * t; i++)
            for (int j = 0; j < t + 1; j++)
                for (int k = 0; k < s * t; k++)
                    addition_relocation[i][j][k] = 0.0;
        for (int i = 0; i < s * t; i++) {
            int cur_N = i + 1; // The current N
            if (cur_N > t) { //When the current N is larger than t-1, the resutls will be meaningful.
                for (int j = 0; j < t + 1; j++) {
                    int cur_k = j + 1;
                    int number_already_moved = s * t - cur_N;
                    if (cur_k == 1) { //for the case the number of empty slots is equal to 1.
                        for (int k = 0; k < s * t; k++) {
                            int cur_n = k + 1;
                            cur_n = cur_n - number_already_moved;
                            if (cur_n > 0) {
                                double temp = (double) (cur_N - (t + 1 - cur_k) - (cur_n - 1)) / (double) (cur_N - (t + 1 - cur_k));
                                if (temp > 0)
                                    addition_relocation[i][j][k] = temp;
                            }
                        }
                    }
                    if (cur_k > 1) {//for the case the number of empty slots is larger than 1.
                        for (int k = 0; k < s * t; k++) {
                            int cur_n = k + 1;
                            cur_n = cur_n - number_already_moved;
                            if (cur_n > 0) {
                                double part1 = 0.0;
                                for (int h = 0; h < k - 1; h++) {
                                    part1 = part1 + addition_relocation[i][j - 1][h] / (double) (cur_N - (t + 1 - cur_k));
                                }
                                double part2 = 0.0;
                                part2 = (double) (cur_N - (t + 1 - cur_k) - (cur_n - 1)) * (1 + addition_relocation[i][j - 1][k]) / (cur_N - (t + 1 - cur_k));
                                addition_relocation[i][j][k] = part1 + part2;
                                //addition_relocation[i][j][k] = (cur_N - (t - cur_k) - (cur_n - 1))/ (cur_N - (t - cur_k));
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < s * t; i++)
            for (int j = 0; j < t + 1; j++)
                for (int k = 0; k < s * t; k++)
                    System.out.println(i + " " + j + " " + k + " " + addition_relocation[i][j][k]);
        return addition_relocation;
    }

    //calculate the rehandling number
    void calculate_rehandle(int s, int t, String[] gn) {
        Calculate_total_rehandle_prepare dataarray = new Calculate_total_rehandle_prepare();
        dataarray.datainput_simplified("store_value" + String.valueOf(s) + String.valueOf(t) + String.valueOf(gn.length) + ".txt", gn);
        //for(int i=0; i<dataarray.size; i++)
        //System.out.println(i+" "+dataarray.next_value[i][1]);

        String[] string_generate = dataarray.generate_string(s, t, gn);
        total_sequence_size = string_generate.length;
        int[] total_rehandle = new int[string_generate.length];
        for (int i = 0; i < total_rehandle.length; i++)
            total_rehandle[i] = 0;
        int final_total_rehandle = 0;
        long begin_time8, end_time8, duration8;
        Date mydate = new Date();
        begin_time8 = mydate.getTime();
        double[][][] expected_addition = expected_addition(s, t);
        for (int i = 0; i < string_generate.length; i++) {
            //if(i==35){
            String target = string_generate[i];
            String[][] bay_state = new String[s][2]; //used to describe the state of the state
            for (int k = 0; k < s; k++) {
                bay_state[k][0] = String.valueOf(t); //initialize bay_state
                bay_state[k][1] = "*";
            }
            int[][] position = new int[target.length()][4]; // used to describe the positions of the containers
            String cur_nc = "";
            String cur_sc = "";
            //the initial state
            for (int k = 0; k < s; k++) {
                cur_nc = cur_nc + String.valueOf(t);
                cur_sc = cur_sc + "*";
            }
            int start_index = dataarray.size - 1;
            String cur_nc_sc_temp = cur_nc + cur_sc;

            //count the number of containers for each kind of weight group
            int[] number_each_weight = new int[gn.length];
            for (int h = 0; h < gn.length; h++)
                number_each_weight[h] = 0;
            for (int j = 0; j < target.length(); j++) {
                for (int h = 0; h < gn.length; h++) {
                    if (String.valueOf(target.charAt(j)).equals(gn[h]))
                        number_each_weight[h] = number_each_weight[h] + 1;
                }
            }
            for (int h = 1; h < gn.length; h++)
                number_each_weight[h] = number_each_weight[h] + number_each_weight[h - 1]; //number_each_weight stores the cumulative number
            for (int h = gn.length - 1; h > 0; h--)
                number_each_weight[h] = number_each_weight[h - 1] + 1; //record the starting number, i.e., the total number of containers of the previous weight group.
            number_each_weight[0] = 1;

            for (int j = 0; j < target.length(); j++) {
                int target_index = 0;
                //find the target index
                for (int k = start_index; k >= 0; k--) {
                    if (cur_nc_sc_temp.equals(dataarray.current_state[k][0])) {
                        target_index = k;
                        break;
                    }
                }
                //find the position of the target weight group
                int target_k = 0;
                for (int k = 0; k < gn.length; k++) {
                    if (String.valueOf(target.charAt(j)).equals(gn[k])) {
                        target_k = k;
                        break;
                    }
                }
                cur_nc_sc_temp = dataarray.next_state[target_index][target_k];
                //cur_sc = dataarray.next_state[target_index][2*target_k+1];
                int stack_no = 0;
                for (int k = 0; k < s; k++) {
                    if (bay_state[k][0].equals(String.valueOf(dataarray.state_number_chosen[target_index][target_k])))
                        if (bay_state[k][1].equals(dataarray.state_weight_chosen[target_index][target_k])) {
                            //if(Math.random()<0.5){
                            stack_no = k;
                            break;
                            //}
                            //if(k==s-1)
                            //   k=0;
                        }
                }
                bay_state[stack_no][0] = String.valueOf(Integer.parseInt(bay_state[stack_no][0]) - 1);
                int target_state_weight_chosen = gn.length;  //the inital position
                for (int k = 0; k < gn.length; k++) {
                    if ((dataarray.state_weight_chosen[target_index][target_k]).equals(gn[k])) {
                        target_state_weight_chosen = k;
                        break;
                    }
                }
                //System.out.println("comapre   "+ (target_k-target_state_weight_chosen));
                if (bay_state[stack_no][0].equals("0"))
                    bay_state[stack_no][1] = "0";
                else {
                    if (target_state_weight_chosen > target_k) // the weight of the arriving container is heavier than that of the original one
                        bay_state[stack_no][1] = String.valueOf(target.charAt(j));
                }

                if (target_k == gn.length - 1)
                    position[j][0] = 1;
                if (target_k == gn.length - 2)
                    position[j][0] = 10;
                if (target_k == gn.length - 3)
                    position[j][0] = 100;
                if (target_k == gn.length - 4)
                    position[j][0] = 1000;
                position[j][1] = stack_no;
                position[j][2] = dataarray.state_number_chosen[target_index][target_k];
                //position[j][3] = number_each_weight[target_k];
                //number_each_weight[target_k] = number_each_weight[target_k] - 1;

            }
            for (int k = 0; k < s; k++)
                for (int h = 0; h < t; h++)
                    for (int j = 0; j < target.length(); j++) {
                        if (position[j][1] == k && position[j][2] == h + 1) {
                            int weight_index = 0;
                            for (int v = 0; v < gn.length; v++)
                                if (String.valueOf(target.charAt(j)).equals(gn[v]))
                                    weight_index = v;
                            position[j][3] = number_each_weight[weight_index];
                            number_each_weight[weight_index] = number_each_weight[weight_index] + 1;
                        }

                    }
            total_rehandle[i] = evaluate_objective(s, t, position, expected_addition);
            System.out.println(i + " " + target + " " + total_rehandle[i]);
            final_total_rehandle = final_total_rehandle + total_rehandle[i];
        }
        Date mydate2 = new Date();
        end_time8 = mydate2.getTime();
        duration8 = end_time8 - begin_time8;
        System.out.println("dynamic total rehandle times:   " + final_total_rehandle);
        total_multiplied_value = final_total_rehandle;
        myproperties p = new myproperties();
        try {
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "dynamic_total_rehandel_times" + String.valueOf(s) + String.valueOf(t) + String.valueOf(gn.length) + ".txt", false);
            PrintWriter write = new PrintWriter(fw);
            write.println(s + "\t" + t + "\t" + string_generate.length + "\t" + final_total_rehandle + "\t" + duration8 + "\t");
            fw.close();
            write.close();
        } catch (IOException f) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("error", f.toString());
        }
    }

    int evaluate_objective(int s, int t, int[][] position, double[][][] expected_addition) {
        int rehandle_number = 0;  // The number of realized relocations which the objective.

        // The state of the bay.
        int[][] bay_state_simulation = new int[s][2];
        for (int i = 0; i < s; i++) {
            bay_state_simulation[i][0] = 1;  //record the number of empty slots in each stack. By default, the average height of each stack is set as t+1.
            bay_state_simulation[i][1] = position.length;  //record the highest priority of the containter stacked in each stack.
        }
        for (int i = 0; i < position.length; i++) {
            for (int j = 0; j < s; j++) {
                if (position[i][1] == j && bay_state_simulation[j][1] > position[i][3])
                    bay_state_simulation[j][1] = position[i][3];
            }
        }
        for (int i = 0; i < position.length; i++) {
            int cur_no = i + 1; // the target container
            int target_stack_no = 0;
            int target_tier_no = 0;
            for (int j = 0; j < position.length; j++) {
                if (position[j][3] == cur_no) {
                    target_stack_no = position[j][1];
                    target_tier_no = position[j][2];
                }
            }
            int target_tier_total = bay_state_simulation[target_stack_no][0]; //the highest tier of the target stack. The tier no is increasing from top to bottom.
            if (target_tier_total < target_tier_no) {//this means there are containers needed to be relocated.
                for (int k = target_tier_no - target_tier_total; k > 0; k--) {//relocate the containers sequentially from top to the one just stacked on the target one.
                    int relocated_index = 0; //record the index of the container to be relocated.
                    for (int j = 0; j < position.length; j++) {
                        if (position[j][1] == target_stack_no && position[j][2] == target_tier_no - k)
                            relocated_index = j;
                    }

                    //find the stack with the minimum objective value.
                    double objective_temp = Double.MAX_VALUE;
                    int stack_chosen_temp = 0;
                    for (int j = 0; j < s; j++) {
                        if (j != target_stack_no && bay_state_simulation[j][0] > 0) { // bay_state_simulation[j][0] > 0 means the stack has at least one empty slot.
                            double the_previous = expected_addition[position.length - cur_no][bay_state_simulation[j][0] - 1][bay_state_simulation[j][1] - 1];
                            double the_new;
                            if (bay_state_simulation[j][0] == 1) { // the case that when the relocated container is stacked this this stack, and then this empty is full.
                                if (position[relocated_index][3] < bay_state_simulation[j][1])
                                    the_new = 0.0;
                                else
                                    the_new = 1.0;
                            } else {
                                if (position[relocated_index][3] < bay_state_simulation[j][1])
                                    the_new = expected_addition[position.length - cur_no][bay_state_simulation[j][0] - 1 - 1][position[relocated_index][3] - 1];
                                else
                                    the_new = 1.0 + expected_addition[position.length - cur_no][bay_state_simulation[j][0] - 1 - 1][bay_state_simulation[j][1] - 1];
                            }
                            if (objective_temp > the_new - the_previous) {
                                objective_temp = the_new - the_previous;
                                stack_chosen_temp = j;
                            }
                        }
                    }
                    //update the bay state of the relocated stack and execute the relocation operation.
                    bay_state_simulation[stack_chosen_temp][0] = bay_state_simulation[stack_chosen_temp][0] - 1;
                    if (position[relocated_index][3] < bay_state_simulation[stack_chosen_temp][1])
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
            for (int j = 0; j < position.length; j++) {
                if (position[j][3] == cur_no) {
                    position[j][1] = -1;
                    position[j][2] = -1;
                }
                if (bay_state_simulation[target_stack_no][1] > position[j][3] && position[j][1] == target_stack_no)
                    bay_state_simulation[target_stack_no][1] = position[j][3];
            }
        }
        return rehandle_number;
    }


}

class Evaluating_rehandling {
    int total_multiplied_value;
    int total_sequence_size;

    int evaluate_objective(int[][] input) {
        int objective_value = 0;
        for (int i = 0; i < input.length; i++) {
            int current_container = input[i][0];
            for (int j = 0; j < input.length; j++) {
                if (current_container > input[j][0]) // the jth container is lighter than the ith one.
                    if (input[i][1] == input[j][1] && input[i][2] > input[j][2]) {
                        //the jth and ith containers are in the same stack and the ith one is stacked below the jth one.
                        // the smaller value of input[i][2], the higher tier
                        /*
                        objective_value = objective_value + 1; //the original evaluation methods
                         */
                        objective_value = objective_value + (input[i][2] - input[j][2]);
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

    void evaluate_function_new(int s, int t, String[] gn) {
        Calculate_total_rehandle_prepare dataarray = new Calculate_total_rehandle_prepare();
        dataarray.datainput("store_value" + String.valueOf(s) + String.valueOf(t) + String.valueOf(gn.length) + ".txt", gn);
        //for(int i=0; i<dataarray.size; i++)
        //System.out.println(i+" "+dataarray.next_value[i][1]);

        String[] string_generate = dataarray.generate_string(s, t, gn);
        total_sequence_size = string_generate.length;
        int[] total_rehandle = new int[string_generate.length];
        for (int i = 0; i < total_rehandle.length; i++)
            total_rehandle[i] = 0;
        int final_total_rehandle = 0;
        long begin_time8, end_time8, duration8;
        Date mydate = new Date();
        begin_time8 = mydate.getTime();
        for (int i = 0; i < string_generate.length; i++) {
            String target = string_generate[i];
            String[][] bay_state = new String[s][2]; //used to describe the state of the state
            for (int k = 0; k < s; k++) {
                bay_state[k][0] = String.valueOf(t); //initialize bay_state
                bay_state[k][1] = "*";
            }
            int[][] position = new int[target.length()][3]; // used to describe the positions of the containers
            String cur_nc = "";
            String cur_sc = "";
            //the initial state
            for (int k = 0; k < s; k++) {
                cur_nc = cur_nc + String.valueOf(t);
                cur_sc = cur_sc + "*";
            }
            int start_index = dataarray.size - 1;
            for (int j = 0; j < target.length(); j++) {
                int target_index = 0;
                //find the target index
                for (int k = start_index; k >= 0; k--) {
                    if (cur_nc.equals(dataarray.current_state[k][0]) && cur_sc.equals(dataarray.current_state[k][1])) {
                        target_index = k;
                        break;
                    }
                }
                //find the position of the target weight group
                int target_k = 0;
                for (int k = 0; k < gn.length; k++) {
                    if (String.valueOf(target.charAt(j)).equals(gn[k])) {
                        target_k = k;
                        break;
                    }
                }
                cur_nc = dataarray.next_state[target_index][2 * target_k + 0];
                cur_sc = dataarray.next_state[target_index][2 * target_k + 1];
                int stack_no = 0;
                for (int k = 0; k < s; k++) {
                    if (bay_state[k][0].equals(String.valueOf(dataarray.state_number_chosen[target_index][target_k])))
                        if (bay_state[k][1].equals(dataarray.state_weight_chosen[target_index][target_k])) {
                            //if(Math.random()<0.5){
                            stack_no = k;
                            break;
                            //}
                            //if(k==s-1)
                            //   k=0;
                        }
                }
                bay_state[stack_no][0] = String.valueOf(Integer.parseInt(bay_state[stack_no][0]) - 1);
                int target_state_weight_chosen = gn.length;  //the inital position
                for (int k = 0; k < gn.length; k++) {
                    if ((dataarray.state_weight_chosen[target_index][target_k]).equals(gn[k])) {
                        target_state_weight_chosen = k;
                        break;
                    }
                }
                //System.out.println("comapre   "+ (target_k-target_state_weight_chosen));
                if (bay_state[stack_no][0].equals("0"))
                    bay_state[stack_no][1] = "0";
                else {
                    if (target_state_weight_chosen > target_k) // the weight of the arriving container is heavier than that of the original one
                        bay_state[stack_no][1] = String.valueOf(target.charAt(j));
                }

                if (target_k == gn.length - 1)
                    position[j][0] = 1;
                if (target_k == gn.length - 2)
                    position[j][0] = 10;
                if (target_k == gn.length - 3)
                    position[j][0] = 100;
                if (target_k == gn.length - 4)
                    position[j][0] = 1000;
                position[j][1] = stack_no;
                position[j][2] = dataarray.tier_no[target_index][target_k];
                //System.out.println("bay_state   "+ position[j][0]+"   "+ position[j][1] + "   "+ position[j][2]);
                //total_rehandle[i] = total_rehandle[i] + position[j][0]*position[j][1];
                //System.out.println("next value:   "+cur_nc + "  "+cur_sc+"  "+dataarray.next_value[target_index][target_k]);
            }
            total_rehandle[i] = evaluate_objective(position);
            System.out.println(i + "  " + target + "  " + total_rehandle[i]);
            final_total_rehandle = final_total_rehandle + total_rehandle[i];
        }
        Date mydate2 = new Date();
        end_time8 = mydate2.getTime();
        duration8 = end_time8 - begin_time8;
        System.out.println("total rehandle times:   " + final_total_rehandle);
        total_multiplied_value = final_total_rehandle;
        myproperties p = new myproperties();
        try {
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "total_rehandel_times" + String.valueOf(s) + String.valueOf(t) + String.valueOf(gn.length) + ".txt", false);
            PrintWriter write = new PrintWriter(fw);
            write.println(s + "\t" + t + "\t" + string_generate.length + "\t" + final_total_rehandle + "\t" + duration8 + "\t");
            fw.close();
            write.close();
        } catch (IOException f) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("error", f.toString());
        }
    }

    void evaluate_function_simplified(int s, int t, String[] gn) {
        Calculate_total_rehandle_prepare dataarray = new Calculate_total_rehandle_prepare();

//读取动态规划的结果表
        dataarray.datainput_simplified("store_value" + String.valueOf(s) + String.valueOf(t) + String.valueOf(gn.length) + ".txt", gn);
        //for(int i=0; i<dataarray.size; i++)
        //System.out.println(i+" "+dataarray.next_value[i][1]);

        String[] string_generate = dataarray.generate_string(s, t, gn);
        total_sequence_size = string_generate.length;
        int[] total_rehandle = new int[string_generate.length];
        for (int i = 0; i < total_rehandle.length; i++)
            total_rehandle[i] = 0;
        int final_total_rehandle = 0;
        long begin_time8, end_time8, duration8;
        Date mydate = new Date();
        begin_time8 = mydate.getTime();
        for (int i = 0; i < string_generate.length; i++) {
            String target = string_generate[i];
            String[][] bay_state = new String[s][2]; //used to describe the state of the state
            for (int k = 0; k < s; k++) {
                bay_state[k][0] = String.valueOf(t); //initialize bay_state
                bay_state[k][1] = "*";
            }
            int[][] position = new int[target.length()][3]; // used to describe the positions of the containers
            String cur_nc = "";
            String cur_sc = "";
            //the initial state
            for (int k = 0; k < s; k++) {
                cur_nc = cur_nc + String.valueOf(t);
                cur_sc = cur_sc + "*";
            }
            int start_index = dataarray.size - 1;
            String cur_nc_sc_temp = cur_nc + cur_sc;

            ////根据动态规划表排布当前序列
            for (int j = 0; j < target.length(); j++) {
                int target_index = 0;
                //find the target index
                for (int k = start_index; k >= 0; k--) {
                    if (cur_nc_sc_temp.equals(dataarray.current_state[k][0])) {
                        target_index = k;
                        break;
                    }
                }
                //find the position of the target weight group
                int target_k = 0;
                for (int k = 0; k < gn.length; k++) {
                    if (String.valueOf(target.charAt(j)).equals(gn[k])) {
                        target_k = k;
                        break;
                    }
                }
                cur_nc_sc_temp = dataarray.next_state[target_index][target_k];
                //cur_sc = dataarray.next_state[target_index][2*target_k+1];
                int stack_no = 0;
                for (int k = 0; k < s; k++) {
                    if (bay_state[k][0].equals(String.valueOf(dataarray.state_number_chosen[target_index][target_k])))
                        if (bay_state[k][1].equals(dataarray.state_weight_chosen[target_index][target_k])) {
                            //if(Math.random()<0.5){
                            stack_no = k;
                            break;
                            //}
                            //if(k==s-1)
                            //   k=0;
                        }
                }
                bay_state[stack_no][0] = String.valueOf(Integer.parseInt(bay_state[stack_no][0]) - 1);
                int target_state_weight_chosen = gn.length;  //the inital position
                for (int k = 0; k < gn.length; k++) {
                    if ((dataarray.state_weight_chosen[target_index][target_k]).equals(gn[k])) {
                        target_state_weight_chosen = k;
                        break;
                    }
                }
                //System.out.println("comapre   "+ (target_k-target_state_weight_chosen));
                if (bay_state[stack_no][0].equals("0"))
                    bay_state[stack_no][1] = "0";
                else {
                    if (target_state_weight_chosen > target_k) // the weight of the arriving container is heavier than that of the original one
                        bay_state[stack_no][1] = String.valueOf(target.charAt(j));
                }

                if (target_k == gn.length - 1)
                    position[j][0] = 1;
                if (target_k == gn.length - 2)
                    position[j][0] = 10;
                if (target_k == gn.length - 3)
                    position[j][0] = 100;
                if (target_k == gn.length - 4)
                    position[j][0] = 1000;
                position[j][1] = stack_no;
                position[j][2] = dataarray.state_number_chosen[target_index][target_k];
                //System.out.println("bay_state   "+ position[j][0]+"   "+ position[j][1] + "   "+ position[j][2]);
                //total_rehandle[i] = total_rehandle[i] + position[j][0]*position[j][1];
                //System.out.println("next value:   "+cur_nc + "  "+cur_sc+"  "+dataarray.next_value[target_index][target_k]);
            }
            //计算当前排布的rehandle
            total_rehandle[i] = evaluate_objective(position);
            System.out.println(i + "  " + target + "  " + total_rehandle[i]);
            final_total_rehandle = final_total_rehandle + total_rehandle[i];
        }
        Date mydate2 = new Date();
        end_time8 = mydate2.getTime();
        duration8 = end_time8 - begin_time8;
        System.out.println("total rehandle times:   " + final_total_rehandle);
        total_multiplied_value = final_total_rehandle;
        myproperties p = new myproperties();
        try {
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "total_rehandel_times" + String.valueOf(s) + String.valueOf(t) + String.valueOf(gn.length) + ".txt", false);
            PrintWriter write = new PrintWriter(fw);
            write.println(s + "\t" + t + "\t" + string_generate.length + "\t" + final_total_rehandle + "\t" + duration8 + "\t");
            fw.close();
            write.close();
        } catch (IOException f) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("error", f.toString());
        }
    }

    void evaluate_function(int s, int t, String[] gn) {
        Calculate_total_rehandle_prepare dataarray = new Calculate_total_rehandle_prepare();
        dataarray.datainput("store_value" + String.valueOf(s) + String.valueOf(t) + ".txt", gn);
        //for(int i=0; i<dataarray.size; i++)
        //System.out.println(i+" "+dataarray.next_value[i][1]);

        String[] string_generate = dataarray.generate_string(s, t, gn);
        int[] total_rehandle = new int[string_generate.length];
        for (int i = 0; i < total_rehandle.length; i++)
            total_rehandle[i] = 0;
        int final_total_rehandle = 0;
        for (int i = 0; i < string_generate.length; i++) {
            String target = string_generate[i];
            int[][] position = new int[target.length()][2];
            String cur_nc = "";
            String cur_sc = "";
            //the initial state
            for (int k = 0; k < s; k++) {
                cur_nc = cur_nc + String.valueOf(t);
                cur_sc = cur_sc + "*";
            }
            for (int j = 0; j < target.length(); j++) {
                int target_index = 0;
                //find the target index
                for (int k = 0; k < dataarray.size; k++) {
                    if (cur_nc.equals(dataarray.current_state[k][0]) && cur_sc.equals(dataarray.current_state[k][1])) {
                        target_index = k;
                        break;
                    }
                }
                //find the position of the target weight group
                int target_k = 0;
                for (int k = 0; k < gn.length; k++) {
                    if (String.valueOf(target.charAt(j)).equals(gn[k])) {
                        target_k = k;
                        break;
                    }
                }
                cur_nc = dataarray.next_state[target_index][2 * target_k + 0];
                cur_sc = dataarray.next_state[target_index][2 * target_k + 1];
                position[j][0] = dataarray.tier_no[target_index][target_k];
                if (target_k == gn.length - 1)
                    position[j][1] = 1;
                if (target_k == gn.length - 2)
                    position[j][1] = 10;
                if (target_k == gn.length - 3)
                    position[j][1] = 100;
                if (target_k == gn.length - 4)
                    position[j][1] = 1000;
                total_rehandle[i] = total_rehandle[i] + position[j][0] * position[j][1];
                //System.out.println("next value:   "+cur_nc + "  "+cur_sc+"  "+dataarray.next_value[target_index][target_k]);
            }
            System.out.println(i + "  " + target + "  " + total_rehandle[i]);
            final_total_rehandle = final_total_rehandle + total_rehandle[i];
        }
        System.out.println("total rehandle times:   " + final_total_rehandle);
        total_multiplied_value = final_total_rehandle;
        myproperties p = new myproperties();
        try {
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "total_rehandel_times" + String.valueOf(s) + String.valueOf(t) + ".txt", false);
            PrintWriter write = new PrintWriter(fw);
            write.println(s + "\t" + t + "\t" + string_generate.length + "\t" + final_total_rehandle + "\t");
            fw.close();
            write.close();
        } catch (IOException f) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("error", f.toString());
        }
    }
}

//.txt文件输入
class Input {
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

class Node {
    String current_state;
    //String
    Node next_node1;
    Node next_node2;

    Node get(String weight_group) {
        if (weight_group.equals("H"))
            return next_node1;
        else
            return next_node2;
    }
}

/**
 * @author zcr
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Dynamic_programming dynamic_programming = new Dynamic_programming();


        myproperties p = new myproperties();
        double[] ratio = {0.5, 0.5};
        String[] gn = {"H", "L"};
/*
        //dyanmic programming to calculate the optimal locations.

        long begin_time, end_time, duration;
        try{
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "final2.txt",false);
            PrintWriter write = new PrintWriter(fw);
            for(int s=4; s<8; s++)
                for(int t=3; t<5; t++){
                    //if(s*t<=12){
                    Date mydate = new Date();
                    begin_time = mydate.getTime();
                    dynamic_programming.dynamic_programming_simplified(s, t, gn, ratio, true);
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
*/

        //static indicator
        long begin_time1, end_time1, duration1;
        try {
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "evaluation2.txt", false);
            PrintWriter write = new PrintWriter(fw);
            for (int s = 4; s < 5; s++)
                for (int t = 3; t < 4; t++) {
                    //if(s*t<=12){
                    Date mydate = new Date();
                    begin_time1 = mydate.getTime();
                    Evaluating_rehandling evaluate = new Evaluating_rehandling();
                    evaluate.evaluate_function_simplified(s, t, gn);
                    Date mydate2 = new Date();
                    end_time1 = mydate2.getTime();
                    duration1 = end_time1 - begin_time1;
                    write.println(s + "\t" + t + "\t" + 2 + "\t" + evaluate.total_sequence_size + "\t" + evaluate.total_multiplied_value + "\t" + duration1);
                }
            fw.close();
            write.close();
        } catch (IOException f) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("error", f.toString());
        }

  /*
        //dynamic indicator
         long begin_time5, end_time5, duration5;
        try{
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "dynamic2.txt",false);
            PrintWriter write = new PrintWriter(fw);
            for(int s=4; s<8; s++)
                    for(int t=3; t<5; t++){
                        //if(s*t<=12){
                        Date mydate = new Date();
                    begin_time5 = mydate.getTime();
                    Evaluating_simulation evaluating_simulation = new Evaluating_simulation();
                    evaluating_simulation.calculate_rehandle(s, t, gn);
                    Date mydate2 = new Date();
                    end_time5 = mydate2.getTime();
                    duration5 = end_time5 - begin_time5;
                    write.println(s+"\t"+t+"\t"+2+"\t"+evaluating_simulation.total_sequence_size+"\t"+evaluating_simulation.total_multiplied_value+"\t"+duration5);
                    }
            fw.close();
            write.close();
            }catch(IOException f){
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("error", f.toString());
            }
 */

        double[] ratio2 = {0.33, 0.33, 0.33};
        //double[] ratio2 ={(double)1/3, (double)1/3, (double)1/3};
        String[] gn2 = {"H", "M", "L"};


        //for the case with three weight groups.
        //dynamic programming to calculate the optimal locations.
        long begin_time2, end_time2, duration2;
        try {
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "final3.txt", false);
            PrintWriter write = new PrintWriter(fw);
            for (int s = 4; s < 5; s++)
                for (int t = 3; t < 4; t++) {
                    //if(s*t<=12){
                    Date mydate = new Date();
                    begin_time2 = mydate.getTime();
                    dynamic_programming.dynamic_programming_simplified(s, t, gn2, ratio2, true);
                    Date mydate2 = new Date();
                    end_time2 = mydate2.getTime();
                    duration2 = end_time2 - begin_time2;

                    write.println(s + "\t" + t + "\t" + 3 + "\t" + dynamic_programming.state_size + "\t" + dynamic_programming.final_value + "\t" + duration2);

                }
            fw.close();
            write.close();
        } catch (IOException f) {
            dialogbox dialog = new dialogbox();
            dialog.dialogbox("error", f.toString());
        }

        /*
        //the static indicator. 
        long begin_time3, end_time3, duration3;
        try{
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "evaluation3.txt",false);
            PrintWriter write = new PrintWriter(fw);
            for(int s=4; s<5; s++)
                    for(int t=3; t<4; t++){
                        //if(s*t<=12){
                        Date mydate = new Date();
                    begin_time3 = mydate.getTime();
                    Evaluating_rehandling evaluate = new Evaluating_rehandling();
                    evaluate.evaluate_function_simplified(s, t, gn2);
                    Date mydate2 = new Date();
                    end_time3 = mydate2.getTime();
                    duration3 = end_time3 - begin_time3;
                    write.println(s+"\t"+t+"\t"+3+"\t"+evaluate.total_sequence_size+"\t"+evaluate.total_multiplied_value+"\t"+duration3);
                    }
            fw.close();
            write.close();
            }catch(IOException f){
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("error", f.toString());
            }
        */

  /*
        //the dynamic indicator.
        long begin_time4, end_time4, duration4;
        try{
            String path = p.getproperties("path");
            FileWriter fw = new FileWriter(path + "dynamic3.txt",false);
            PrintWriter write = new PrintWriter(fw);
            for(int s=4; s<5; s++)
                    for(int t=3; t<4; t++){
                        //if(s*t<=12){
                        Date mydate = new Date();
                    begin_time4 = mydate.getTime();
                    Evaluating_simulation evaluating_simulation = new Evaluating_simulation();
                    evaluating_simulation.calculate_rehandle(s, t, gn2);
                    Date mydate2 = new Date();
                    end_time4 = mydate2.getTime();
                    duration4 = end_time4 - begin_time4;
                    write.println(s+"\t"+t+"\t"+3+"\t"+evaluating_simulation.total_sequence_size+"\t"+evaluating_simulation.total_multiplied_value+"\t"+duration4);
                    }
            fw.close();
            write.close();
            }catch(IOException f){
                dialogbox dialog = new dialogbox();
                dialog.dialogbox("error", f.toString());
            }

 */


        /*
         //searching method test.
        int s=2;
        int t=2;
        Calculate_total_rehandle_prepare datainput = new Calculate_total_rehandle_prepare();
        datainput.datainput("store_value"+String.valueOf(s)+String.valueOf(t)+".txt", gn);

        Node[] node = new Node[datainput.size];
        for(int i=0; i<datainput.size; i++)
            node[i] = new Node();
        for(int i=0; i<datainput.size; i++)
            node[i].current_state = datainput.current_state[i][0] + datainput.current_state[i][1];
        for(int i=datainput.size-1; i>=0; i--)
            for(int k=0; k<gn.length; k++){
                for(int j=i; j>=0; j--){
                    if((datainput.next_state[i][2*k+0]+datainput.next_state[i][2*k+1]).equals(datainput.current_state[j][0]+datainput.current_state[j][1]) && k==0){
                        node[i].next_node1 = node[j];
                        break;
                    }
                    if((datainput.next_state[i][2*k+0]+datainput.next_state[i][2*k+1]).equals(datainput.current_state[j][0]+datainput.current_state[j][1]) && k==1){
                        node[i].next_node2 = node[j];
                        break;
                    }
                }
        }
        
        System.out.println(node[datainput.size-1].get("H").get("L").get("L").current_state);
*/

    }

}

