package test;

import com.highgo.platform.apiserver.controller.InstanceController;
import com.highgo.platform.apiserver.model.vo.response.InstanceVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author srk
 * @version 1.0
 * @project ivory-cloud
 * @description functional test
 * @date 2023/10/27 09:02:20
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = InstanceController.class)
//@WebMvcTest(InstanceController.class)
@ActiveProfiles("test")
public class ITFunctionalTest {

	@Resource
	private InstanceController controller;

	@Test
	public void testExample() {
//		//groupManager访问路径
//		CreateInstanceVO  vo = new CreateInstanceVO();
//		vo.setAdmin("sysdba");
//		vo.setCpu(1);
//		vo.setMemory(1);
//		vo.setCreator("3");
//		vo.setName("demo");
//		vo.setClusterId("ef9d1dcfe80f440b9fa67a7ef2fcd30b");
//		vo.setStorageClass("incloud-nfs");
//		vo.setType(InstanceType.ALONE);
//		vo.setStorage(1);
//		vo.setCreatorName("demo");
//		vo.setNamespace("ivory");
//		vo.setPassword("Hello@123");
//		instanceController.createInstance(vo);

		System.out.println("--------------------------------------");
//		controller.test();

		List<InstanceVO> list = controller.list("3");
		System.out.println(list);
//		CreateInstanceVO  vo = new CreateInstanceVO();
//		MvcResult result=mvc.perform(MockMvcRequestBuilders.post("/instances")).andReturn();
//
//
//		MockHttpServletResponse response = result.getResponse();
//		String content = response.getContentAsString();

	}



}


