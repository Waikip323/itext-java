/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.counter;

/**
 * Factory that can be registered in {@link EventCounterHandler} and creates a counter for every reader or writer class.
 * <p>
 * You can implement your own counter factory and register it like this:
 * <code>EventCounterHandler.getInstance().register(new SystemOutEventCounterFactory());</code>
 * <p>
 * {@link SystemOutEventCounterFactory} is just an example of {@link IEventCounterFactory} implementation.
 * It creates {@link SystemOutEventCounter} that writes info about files being read and written to the {@link System#out}
 * <p>
 * This functionality can be used to create metrics in a SaaS context.
 */
public interface IEventCounterFactory {

    EventCounter getCounter(Class<?> cls);
}
