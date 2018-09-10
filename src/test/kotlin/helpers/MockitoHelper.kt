package helpers

import org.mockito.invocation.InvocationOnMock

operator fun InvocationOnMock.component1(): Any = arguments[0]
operator fun InvocationOnMock.component2(): Any = arguments[1]
operator fun InvocationOnMock.component3(): Any = arguments[2]
