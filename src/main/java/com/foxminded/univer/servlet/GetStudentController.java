package com.foxminded.univer.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.foxminded.univer.dao.impl.StudentDao;
import com.foxminded.univer.models.Student;

public class GetStudentController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer studentId = Integer.parseInt(request.getParameter("studentId"));
        StudentDao studentDao = new StudentDao();
        Student studentToReturn = studentDao.findById(studentId).get();
        
        request.setAttribute("student", studentToReturn);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("showStudent.jsp");
        requestDispatcher.forward(request, response);
    }
}
