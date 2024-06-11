package com.cramsan.edifikana.client.lib.utils

import com.cramsan.framework.core.CoreUri
import com.cramsan.edifikana.client.lib.utils.IODependencies

expect fun CoreUri.getFilename(ioDependencies: IODependencies): String