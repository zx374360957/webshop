# web网上商城项目

# 一、需求分析
## 1. 用户模块
* 用户注册功能
 * 发送邮件
 * 激活用户
 * 表单的校验


* 用户登录功能
 * 自动登录


* 用户注销功能

## 2. 商品模块
* 首页热门商品和最新商品
* 商品分类
* 商品的列表（分页）
* 商品详情
* 浏览记录

## 3. 购物车模块
* 将商品加入购物车
* 展示购物车功能

## 4. 订单模块（多表和事务）
* 提交订单
* 展示订单
* 在线支付

## 5. 后台的分类的模块
* 分类信息的CRUD

## 6. 后台商品模块
* 商品信息的CRUD

## 7. 后台的订单模块
* 所有订单信息的展示

# 二、数据库表设计
![table](https://raw.githubusercontent.com/weiliangchun/MarkdownPic/master/itcast-webshop/table.jpg)

# 三、项目搭建
1. 创建项目shop
2. 创建项目的包结构
3. 导入需要的jar/配置文件/工具/静态页面
4. coding

# 四、项目书写
## 1. 准备工作
### 1.1 通用Servlet
#### 1.1.1 分析
* 方式1：一个路径对应一个servlet
```
//userFindAllServlet 查询所有
class UserFindAllServlet(){
	doGet(){

	}
}
```

* 方式2：多个路径对应一个servlet，method请求参数与if语句进行分流
```
class UserServlet{
	service(request,response){
		//1. 获得method请求参数
		String method = request.getParameter("method");
		//2. 如果是findAll执行findAll()方法
		if("findAll".equals(method)){
			findAll(request,response);
		} else if("login".equals(method)){
			login(request,response);
		}
	}
}
```

不足之处：service()的编写需要大量的if语句

* 方式3：多个路径对应一个servlet，编写BaseServlet，使用反射执行当前运行类的指定方法
```
class BaseServlet(){
	service(request,response){
		//1. 获得method请求参数
		String methodName = reqeust.getParameter("method");
		//2. 获得当前运行类 this==UserServlet 执行的方法
		//2.1 获得当前运行类的字节码对象
		Class clazz = this.getClass();
		//2.2 获得对应的方法
		Method method = clazz.getMethod(methodName,HttpServletRequest.class,HttpServletResponse.class);
		//2.3 执行当前运行类对应的方法
		method.invoke(this,request,response);
	}
}

class UserServlet extends BaseServlet{
	findAll(request,response){
		//查询所有的具体功能
	}
}
```

* 方式4：完善BaseServlet,当前运行类的指定方法返回请求转发时jsp页面路径
```
class BaseServlet(){
	service(request,response){
		//1. 获得method请求参数
		String methodName = reqeust.getParameter("method");
		//2. 获得当前运行类 this==UserServlet 执行的方法
		//2.1 获得当前运行类的字节码对象
		Class clazz = this.getClass();
		//2.2 获得对应的方法
		Method method = clazz.getMethod(methodName,HttpServletRequest.class,HttpServletResponse.class);
		//2.3 执行当前运行类对应的方法
		method.invoke(this,request,response);
		//2.4 如果有返回值，请求转发到指定的jsp
		if(jspPath!=null){request.getRequestDispatcher(jspPath).forward(request,response);}
	}
}

class UserServlet extends BaseServlet{
	findAll(request,response){
		//查询所有的具体功能
		return "/jsp/list.jsp";
	}
}
```

#### 1.1.2 实现
* 编写BaseServlet类
```
public class BaseServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	public void service(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
		try {
			//1.获得请求参数method
			String methodName = request.getParameter("method");
			// 默认方法名
			if(methodName == null) {
				methodName = "execute";
			}
			//2. 获取当前运行类，需要指定的方法
			Method method = this.getClass().getMethod(methodName, HttpServletRequest.class,HttpServletResponse.class);
			//3. 执行方法
			String jspPath = (String) method.invoke(this, request,response);
			//4. 如果子类有返回值，将请求到指定的jsp页面
			if(jspPath!=null) {
				request.getRequestDispatcher(jspPath).forward(request, response);
			}
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String execute(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
		return null;
	}
}
```
* BaseServlet实现类
```
public class UserServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	public void findAll(HttpServletRequest request,HttpServletResponse response) {
		System.out.println("findAll");
	}
}
```
* web.xml配置
```
<servlet>
  	<servlet-name>UserServlet</servlet-name>
  	<servlet-class>com.weilc.webshop.web.servlet.UserServlet</servlet-class>
  </servlet>
  <servlet-mapping>
  	<servlet-name>UserServlet</servlet-name>
  	<url-pattern>/UserServlet</url-pattern>
  </servlet-mapping>
```
* 测试路径:localhost:8080/webshop/UserServlet?method=findAll

### 1.2 项目结构
![项目结构](https://raw.githubusercontent.com/weiliangchun/MarkdownPic/master/itcast-webshop/1-1.jpg)

### 1.3 导入jar包
![jar包](https://raw.githubusercontent.com/weiliangchun/MarkdownPic/master/itcast-webshop/1-2.jpg)

### 1.4 工具类
![工具类](https://raw.githubusercontent.com/weiliangchun/MarkdownPic/master/itcast-webshop/1-3.jpg)
#### 1.4.1 DataSourceUtils
* 配置c3p0连接池
```
public class DataSourceUtils {

	private static DataSource dataSource = new ComboPooledDataSource();

	private static ThreadLocal<Connection> tl = new ThreadLocal<Connection>();

	// 直接可以获取一个连接池
	public static DataSource getDataSource() {
		return dataSource;
	}

	// 获取连接对象
	public static Connection getConnection() throws SQLException {

		Connection con = tl.get();
		if (con == null) {
			con = dataSource.getConnection();
			tl.set(con);
		}
		return con;
	}

	// 开启事务
	public static void startTransaction() throws SQLException {
		Connection con = getConnection();
		if (con != null) {
			con.setAutoCommit(false);
		}
	}

	// 事务回滚
	public static void rollback() throws SQLException {
		Connection con = getConnection();
		if (con != null) {
			con.rollback();
		}
	}

	// 提交并且 关闭资源及从ThreadLocall中释放
	public static void commitAndRelease() throws SQLException {
		Connection con = getConnection();
		if (con != null) {
			con.commit(); // 事务提交
			con.close();// 关闭资源
			tl.remove();// 从线程绑定中移除
		}
	}

	// 关闭资源方法
	public static void closeConnection() throws SQLException {
		Connection con = getConnection();
		if (con != null) {
			con.close();
		}
	}

	public static void closeStatement(Statement st) throws SQLException {
		if (st != null) {
			st.close();
		}
	}

	public static void closeResultSet(ResultSet rs) throws SQLException {
		if (rs != null) {
			rs.close();
		}
	}

}
```
* 导入c3p0-config.xml配置文件
```
<?xml version="1.0" encoding="UTF-8"?>
<c3p0-config>
	<default-config>
		<property name="user">root</property>
		<property name="password">root</property>
		<property name="driverClass">com.mysql.jdbc.Driver</property>
		<property name="jdbcUrl">jdbc:mysql:///webshop</property>
	</default-config> 
</c3p0-config>

```

#### 1.4.2 MyBeanUtils
* 对BeanUtils进一步封装，同时处理日期转换
* 方式：传递JavaBean Class类型，通过反射进行实例化，然后封装数据
```
public class MyBeanUtils extends BeanUtils{
	public static <T> T populate(Class<T> beanClass,Map<String,String[]> properties) {
		try {
			//1.使用反射创建实例
			T bean = beanClass.newInstance();
			//2.1 创建BeanUtils提供时间转换器
			DateConverter dateConverter = new DateConverter();
			//2.2 设置需要转换的格式
			dateConverter.setPatterns(new String[] {"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss"});
			//2.3 注册转换器
			ConvertUtils.register(dateConverter, java.util.Date.class);
			//3. 封装数据
			BeanUtils.populate(bean, properties);
			
			return bean;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
}
```

#### 1.4.3 UUIDUtils
```
public class UUIDUtils {
	//生成uuid方法
	public static String getUUID(){
		return UUID.randomUUID().toString();
	}
}
```

### 1.5 编码过滤器
![过滤器](https://raw.githubusercontent.com/weiliangchun/MarkdownPic/master/itcast-webshop/1-4.jpg)
* 步骤1：过滤器实现类
```
public class EncodingFilter implements Filter{
	public void init(FilterConfig fConfig) throws ServletException{
		
	}
	
	public void doFilter(ServletRequest req,ServletResponse resp,FilterChain chain) throws IOException,ServletException{
		//1. 强转
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		//2. 设置编码
		request.setCharacterEncoding("utf-8");
		//3. 创建自定义request
		MyRequest myRequest = new MyRequest(request);
		//4. 放行，使用自定义request
		chain.doFilter(myRequest, response);
	}
	
	public void destroy() {
		
	}
}
```

* 步骤2：配置过滤器
```
<filter>
  	<filter-name>EncodingFilter</filter-name>
  	<filter-class>com.weilc.webshop.utils.EncodingFilter</filter-class>
</filter>
<filter-mapping>
  	<filter-name>EncodingFilter</filter-name>
  	<url-pattern>/*</url-pattern>
</filter-mapping>
```

* 步骤3：自定义request实现类，对获得请求参数方法进行处理
```
public class MyRequest extends HttpServletRequestWrapper{
	//是否已经被编码，默认false，没有被编码
	private boolean encoded = false;
	public MyRequest(HttpServletRequest request) {
		super(request);
	}
	
	/**
	 * 获得指定名称的第一个参数
	 */
	public String getParameter(String name) {
		String [] all = getParameterValues(name);
		if(all == null) {
			return null;
		}
		return all[0];
	}
	
	/**
	 * 获得指定名称的所有参数
	 */
	public String[] getParameterValues(String name) {
		return getParameterMap().get(name);
	}
	
	public Map<String , String[]> getParameterMap() {
		try {
			//1. 获得原始数据
			Map<String , String[]> map = super.getParameterMap();
			//2. 如果是get请求，存放栏目
			if(!encoded) {
				if("GET".equalsIgnoreCase(super.getMethod())) {
					//遍历map，并遍历数组值
					for(Map.Entry<String, String[]> entry : map.entrySet()) {
						String[] allValue = entry.getValue();
						for(int i=0;i<allValue.length;i++) {
							String encoding = super.getCharacterEncoding();
							if(encoding == null) {
								encoding = "utf-8";
							}
							allValue[i] = new String(allValue[i].getBytes("ISO-8859-1"),encoding);
						}
					}
					encoded = true;
				}
			}
			return map;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
```

### 1.6 导入JSP页面
![导入JSP页面](https://raw.githubusercontent.com/weiliangchun/MarkdownPic/master/itcast-webshop/1-5.jpg)
* 修改index.jsp页面，使其通过servlet访问jsp
* 步骤1：修改index.jsp
```
<jsp:forward page="/IndexServlet?method=execute"/>
```

* 步骤2：编写servlet实现类
```
public class IndexServlet extends BaseServlet{

	private static final long serialVersionUID = 1L;
	
	public String execute(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		return "/jsp/index.jsp";
	}
}
```

*  步骤3：web.xml配置
```
<servlet>
  	<servlet-name>IndexServlet</servlet-name>
  	<servlet-class>com.weilc.webshop.web.servlet.IndexServlet</servlet-class>
</servlet>
<servlet-mapping>
  	<servlet-name>IndexServlet</servlet-name>
  	<url-pattern>/IndexServlet</url-pattern>
</servlet-mapping>
```

## 2. 用户前台模块
### 2.1 编写流程
* 步骤1：导入sql文件
* 步骤2：创建Javabean
```
	...
private String uid;
private String username;// 用户名
private String password;
private String name; // 昵称
private String email;
private String telephone;
private Date birthday;
private String sex;
private int state; // 状态： 0-未激活 1-已激活
private String code;// 激活码
	...	
```

* 步骤3：编写dao接口及实现类
```
//用户模块的DAO层接口
public interface UserDao{

}
//用户模块的DAO层接口实现类
public class UserDaoImpl implements UserDao{

}
```

* 步骤4：编写service接口，及其实现类
```
//用户模块的service接口
public interface UserService{

}
//用户模块的service层的实现类
public class UserServiceImpl implements UserService{

}
```

* 步骤5：编写servlet
```
public class UserServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	public void findAll(HttpServletRequest request,HttpServletResponse response) {
		System.out.println("findAll");
	}
}

```

### 2.2 实现用户注册功能
#### 2.2.1 显示注册表单
* 步骤1：修改/front/header.jsp内容
```
<a href="${pageContext.request.contextPath}/UserServlet?method=registUI">注册</a>
```

* 步骤2：修改UserServlet,添加registerUI()方法
```
public String registerUI(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
		return "/front/register.jsp";
	}
```

#### 2.2.2 异步校验用户是否存在
##### 2.2.2.1 编写步骤
1. 用户名文本框失去焦点触发JS函数
2. 使用AJAX发送异步请求
3. 获得服务器相应的数据，并处理。将结果显示在文本框后面的span中

##### 2.2.2.2 代码实现
* 步骤1：确定html页面
```
<input type="text" class="form-control" id="username" name="username" placeholder="请输入用户名"> <span id="s1"></span>
```

* 步骤2：给username文本框绑定事件，blur失去焦点触发js
```
<script>
	$(function(){
		$('#username').blur(function(){
			//获取该文本框的值
			var username = $(this).val();
			//异步发送数据
			if(username!=""){
				var url = "${pageContext.request.contextPath}/UserServlet";
				var params = {
						"method":"checkUsername",
						"username":username				
				};
				$.post(url,params,function(data){
					if(data == 1){
						$('#s1').html("用户名可以使用").css("color","#0f0");
						$('#regBut').attr("disabled",false);
					} else if(data == 2){
						$("#s1").html("用户名已经被注册").css("color","#f00");
						$("regBut").attr("disabled",true);
					}
				})
			}
		})
	})
</script>
```

* 步骤3：修改UserServlet，添加 checkUsername()方法
```
public void checkUsername(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException, SQLException{
	//接收文本框的值
	String username = request.getParameter("username");
	//调用业务层查询
	UserService userService = new UserServiceImpl();
	User existUser = userService.findByUsername(username);
	//判断
	if(existUser == null) {
		//用户名没有使用
		response.getWriter().println(1);
	} else {
		//用户名已经被使用
		response.getWriter().println(2);
	}
}
```

* 步骤4：修改service，添加findByUsername()方法
```
//UserService接口
public interface UserService{
	User findByUsername(String username) throws SQLException;
}

//接口实现类
public class UserServiceImpl implements UserService{
	private UserDao userDao = new UserDaoImpl();
	@Override
	public User findByUsername(String username) throws SQLException {
		// TODO Auto-generated method stub
		return userDao.findByUsername(username);
	}
}
```

* 步骤5：修改dao，添加findByUsername()方法
```
//UserDao接口
public interface UserDao{
	User findByUsername(String username) throws SQLException;
}

//接口实现类
public class UserDaoImpl implements UserDao {

	/**
	 * 根据用户名查找用户 
	 */
	@Override
	public User findByUsername(String username) throws SQLException {
		QueryRunner queryRunner = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "select * from user where user name=?";
		User existUser = queryRunner.query(sql, new BeanHandler<User>(User.class));
		return existUser;
	}
	
}
```

#### 2.2.3 用户注册
##### 2.2.3.1 编写步骤
1. 完善register.jsp表单，确定表单元素有name属性
2. 在注册页面输入信息，点击注册，提交到UserServlet()的regist()方法进行处理
3. servlet 调用 service 的 regist(user) 进行用户注册
4. service 调用 dao 的 save(user) 将用户保存到数据库

##### 2.2.3.2 代码实现
* 步骤1：完善表单，确定表单元素的name属性
```
<form action="${pageContext.request.contextPath}/UserServlet?method=regist" method="post">
	<input type="text" name="username" placeholder="请输入用户名" />
	<input type="password" name="password" placeholder="请输入密码" />
	<input type="text" name="confirmpwd" placeholder="请确认密码" />
	<input type="email" name="email" placeholder="Email" />
	<input type="text" name="name" placeholder="请输入姓名" />
	<input type="radio" name="sex" value="男" /> 男
	<input type="radio" name="sex" value="女" /> 女
	<input type="date" name="birthday" />
</form>
```

* 步骤2：修改UserServlet，提供regist(request,response)方法
```
public String regist(HttpServletRequest request,HttpServletResponse response) throws Exception {
	//1. 获得数据并封装
	User user = MyBeanUtils.populate(User.class, request.getParameterMap());
	//1.1 处理服务器自动生成
	user.setUid(UUIDUtils.getUUID());
	user.setCode(UUIDUtils.getUUID64()); //激活码
	user.setState(0); //0:未激活
	
	//2. 处理
	UserService userService = new UserServiceImpl();
	userService.regist(user);
	
	//3. 成功提示
	request.setAttribute("msg", "注册成功，请邮件激活后登录");
	
	//4. 注册成功登录
	return "/front/login.jsp";
}
```

* 步骤3：修改UserService，提供regist(user)方法进行注册
```
//接口
void regist(User user) throws SQLException;

//实现类
@Override
public void regist(User user) throws SQLException {
	userDao.save(user);
}
```

* 步骤4：修改UserDao，提供save(user)方法进行用户信息的保存
```
//接口
void save(User user) throws SQLException;

//实现类
@Override
public void save(User user) throws SQLException {
	QueryRunner queryRunner = new QueryRunner(DataSourceUtils.getDataSource());
	String sql = "insert into user(uid,username,password,name,email,telephone,birthday,sex,state,code) values (?,?,?,?,?,?,?,?,?,?)";
	Object[] params = {user.getUid(),user.getUsername(),user.getPassword(),user.getName(),user.getEmail(),user.getTelephone(),user.getBirthday(),user.getSex(),user.getState(),user.getCode()};
	queryRunner.update(sql,params);
}
```

* 步骤5：修改登录/front/login.jsp页面,添加提示信息
```
<font>会员登录</font>USER LOGIN
<div>
	<font style="color:#f00">${msg}</font>
</div>
```

#### 2.2.4 发送激活邮件
##### 2.2.4.1 编写步骤
1. 注册用户保存到数据库后发送激活邮箱
2. 创建会话，确定连接邮箱服务器的地址
3. 编写消息，确定需要发送的内容
4. 发送消息

##### 2.2.4.2 代码实现
* 步骤1：确定导入mail.jar