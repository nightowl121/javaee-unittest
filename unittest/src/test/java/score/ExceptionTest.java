/*
 * Copyright 2022 ICONLOOP Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package score;

import com.iconloop.score.test.Account;
import com.iconloop.score.test.ServiceManager;
import com.iconloop.score.test.TestBase;
import org.junit.jupiter.api.Test;
import score.annotation.External;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ExceptionTest extends TestBase {
    private static final ServiceManager sm = getServiceManager();
    private static final Account owner = sm.createAccount();
    private static final int ERROR_CODE = 100;

    public static class Callee {
        @External
        public void expectRevert() {
            Context.revert(ERROR_CODE);
        }
    }

    public static class Caller {
        @External
        public void invoke(Address callee) {
            try {
                Context.call(callee, "expectRevert");
            } catch (UserRevertedException e) {
                if (e.getCode() != ERROR_CODE) {
                    Context.revert(0);
                }
            }
        }
    }

    @Test
    void testUserRevert() throws Exception {
        var callee = sm.deploy(owner, Callee.class);
        var caller = sm.deploy(owner, Caller.class);
        assertDoesNotThrow(() -> caller.invoke(owner, "invoke", callee.getAddress()));
    }
}
