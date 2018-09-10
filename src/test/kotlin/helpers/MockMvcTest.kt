package helpers

import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.servlet.view.InternalResourceViewResolver

interface MockMvcTest {
    fun controller(): Any

    fun mockMvc(): MockMvc {
        return MockMvcBuilders
                .standaloneSetup(controller())
                .setViewResolvers(InternalResourceViewResolver())
                .build()
    }
}
